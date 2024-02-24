package com.cfl.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfl.shortlink.project.common.constant.RedisKeyConstant;
import com.cfl.shortlink.project.common.convention.exception.ClientException;
import com.cfl.shortlink.project.common.convention.exception.ServiceException;
import com.cfl.shortlink.project.common.enums.ValidDateTypeEnum;
import com.cfl.shortlink.project.dao.entity.ShortLinkDO;
import com.cfl.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.cfl.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.cfl.shortlink.project.dao.mapper.ShortLinkMapper;
import com.cfl.shortlink.project.dto.req.ShortLInkCreateReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLInkUpdateReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.cfl.shortlink.project.dto.resp.ShortLInkCreateRespDTO;
import com.cfl.shortlink.project.dto.resp.ShortLInkPageRespDTO;
import com.cfl.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.cfl.shortlink.project.service.ShortLinkService;
import com.cfl.shortlink.project.toolkit.HashUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 短链接接口实现层
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    @Override
    public ShortLInkCreateRespDTO createShortLink(ShortLInkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortUrl = StrBuilder.create(requestParam.getDomain())
                .append("/")
                .append(shortLinkSuffix)
                .toString();
        ShortLinkDO shortLinkDO =ShortLinkDO.builder()
                .domain(requestParam.getDomain())
                .shortUri(shortLinkSuffix)
                .fullShortUrl(fullShortUrl)
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .enableStatus(0)
                .describe(requestParam.getDescribe())
                .build();
        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                .gid(requestParam.getGid())
                .fullShortUrl(fullShortUrl)
                .build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(shortLinkGotoDO);
        } catch (DuplicateKeyException ex) {
            // TODO 被误判的短链接如何处理
            // 1.在缓存中真实存在
            // 2.缓存中不存在 但是误判
            LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                            .eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO hasShortLinkDO1 = baseMapper.selectOne(queryWrapper);
            if (hasShortLinkDO1 != null) {
                log.warn("短链接: {} 重复入库", fullShortUrl);
                throw new ServiceException("短链接生成重复");
            }

        }
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        return ShortLInkCreateRespDTO.builder()
                .fullShortUrl("http://" + shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    @Override
    public IPage<ShortLInkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam ,queryWrapper);
        return resultPage.convert(each -> {
            ShortLInkPageRespDTO result = BeanUtil.toBean(each, ShortLInkPageRespDTO.class);
            result.setDomain("http://" + result.getDomain());
            return result;
        });
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> ListGroupShortLinkCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid, count(*) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> shortLinkDOList = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(shortLinkDOList, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLInkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);

        if (hasShortLinkDO == null) throw new ClientException("短链接不存在");

        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(hasShortLinkDO.getDomain())
                .shortUri(hasShortLinkDO.getShortUri())
                .clickNum(hasShortLinkDO.getClickNum())
                .favicon(hasShortLinkDO.getFavicon())
                .createdType(hasShortLinkDO.getCreatedType())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .build();

        if (Objects.equals(hasShortLinkDO.getGid(), requestParam.getGid())) {
            LambdaUpdateWrapper updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .set(Objects.equals(requestParam.getValidDateType(),
                            ValidDateTypeEnum.PERMANENT.getType()), ShortLinkDO::getValidDate, null);

            baseMapper.update(shortLinkDO ,updateWrapper);
        } else {
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, hasShortLinkDO.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            baseMapper.delete(updateWrapper);
            baseMapper.insert(shortLinkDO);
        }

    }

    @SneakyThrows
    @Override
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) {
        String severName = request.getServerName();
        String fullShortUrl = severName + "/" + shortUri;
        String originalLink = stringRedisTemplate.opsForValue().get(String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(originalLink)) {
            ((HttpServletResponse)response).sendRedirect(originalLink);
            return;
        }
        RLock rLock = redissonClient.getLock(String.format(RedisKeyConstant.LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        rLock.lock();
        try {
            //再次获取redis缓存中的链接，防止重复查询数据库
            originalLink = stringRedisTemplate.opsForValue().get(String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(originalLink)) {
                //已查询到redis中含有链接 不需要再查数据库
                ((HttpServletResponse)response).sendRedirect(originalLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
            if (shortLinkGotoDO == null) {
                // TODO 此处需进行风控
                return;
            }

            LambdaQueryWrapper<ShortLinkDO> linkDOQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, shortLinkGotoDO.getFullShortUrl())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);

            ShortLinkDO shortLinkDO = baseMapper.selectOne(linkDOQueryWrapper);
            if (shortLinkDO != null) {
                ((HttpServletResponse)response).sendRedirect(shortLinkDO.getOriginUrl());
            }
        } finally {
            rLock.unlock();
        }
    }

    private String generateSuffix(ShortLInkCreateReqDTO requestParam) {
        int customGenerateCount = 0;
        String shortUri = null;
        while (true) {
            if (customGenerateCount > 10) {
                throw new ServiceException("短链接创建过于频繁, 请稍后再试");
            }
            String originUrl = requestParam.getOriginUrl();
            originUrl += System.currentTimeMillis(); //降低hash碰撞的概率
            shortUri = HashUtil.hashToBase62(originUrl);
            if (!shortUriCreateCachePenetrationBloomFilter.contains(requestParam.getDomain() + "/" + shortUri)) {
                break;
            }
            customGenerateCount++;
        }
        return shortUri;
    }
}
