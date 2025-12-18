package com.xiaozhounandu.service;

import com.xiaozhounandu.dto.response.PageResponse;
import com.xiaozhounandu.entity.FollowUp;

public interface FollowUpService {
    PageResponse<FollowUp> getFollowUpList(Long customerId, Integer page, Integer pageSize);

    int addFollowUp(FollowUp followUp);

    int deleteFollowUp(Long id);

    FollowUp getFollowUpById(Long id);

    Long countTodayFollowUps();

    Long countUpcomingFollowUps();

    long countTotalFollowUps();

    java.util.Map<String, Object> getFollowUpByType();
}
