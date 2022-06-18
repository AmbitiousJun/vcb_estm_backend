package com.ambitious.vcbestm.service;

import com.ambitious.vcbestm.pojo.po.EstimateHistory;

import java.util.List;

/**
 * 评估历史业务
 * @author Ambitious
 * @date 2022/6/18 17:09
 */
public interface EstimateHistoryService {

    /**
     * 查询用户的评估历史列表
     * @param uid 用户 id
     * @return 历史列表
     */
    List<EstimateHistory> findListByUid(Long uid);
}
