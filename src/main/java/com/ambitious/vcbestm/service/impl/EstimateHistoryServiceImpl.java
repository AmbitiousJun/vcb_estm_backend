package com.ambitious.vcbestm.service.impl;

import com.ambitious.vcbestm.mapper.EstimateHistoryMapper;
import com.ambitious.vcbestm.pojo.po.EstimateHistory;
import com.ambitious.vcbestm.service.EstimateHistoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Ambitious
 * @date 2022/6/18 17:10
 */
@Service("estimateHistoryService")
@Transactional(rollbackFor = Exception.class)
public class EstimateHistoryServiceImpl implements EstimateHistoryService {

    @Resource
    private EstimateHistoryMapper estimateHistoryMapper;

    @Override
    public List<EstimateHistory> findListByUid(Long uid) {
        if (uid == null) {
            return null;
        }
        LambdaQueryWrapper<EstimateHistory> qw = new LambdaQueryWrapper<>();
        qw.eq(EstimateHistory::getSid, uid);
        return estimateHistoryMapper.selectList(qw);
    }
}
