package com.xiaozhounandu.service.impl;

import com.xiaozhounandu.dto.response.PageResponse;
import com.xiaozhounandu.entity.FollowUp;
import com.xiaozhounandu.mapper.FollowUpMapper;
import com.xiaozhounandu.service.FollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FollowUpServiceImpl implements FollowUpService {

    @Autowired
    private FollowUpMapper followUpMapper;

    @Override
    public PageResponse<FollowUp> getFollowUpList(Long customerId, Integer page, Integer pageSize) {
        int total = followUpMapper.countByCustomerId(customerId);
        List<FollowUp> list = new ArrayList<>();

        if (total > 0) {
            int offset = (page - 1) * pageSize;
            list = followUpMapper.selectByCustomerId(customerId, offset, pageSize);
        }

        int pages = (total + pageSize - 1) / pageSize;

        return PageResponse.<FollowUp>builder()
                .list(list)
                .total((long) total)
                .page(page)
                .pageSize(pageSize)
                .pages(pages)
                .build();
    }

    @Override
    public int addFollowUp(FollowUp followUp) {
        return followUpMapper.insert(followUp);
    }

    @Override
    public int deleteFollowUp(Long id) {
        return followUpMapper.delete(id);
    }

    @Override
    public FollowUp getFollowUpById(Long id) {
        return followUpMapper.selectById(id);
    }

    @Override
    public Long countTodayFollowUps() {
        return (long) followUpMapper.countTodayFollowUps();
    }

    @Override
    public Long countUpcomingFollowUps() {
        return (long) followUpMapper.countUpcomingFollowUps();
    }

    @Override
    public long countTotalFollowUps() {
        return followUpMapper.countTotalFollowUps();
    }

    @Override
    public Map<String, Object> getFollowUpByType() {
        List<Map<String, Object>> typeList = followUpMapper.getFollowUpByType();
        Map<String, Object> typeMap = new HashMap<>();
        for (Map<String, Object> type : typeList) {
            String name = (String) type.get("name");
            Long count = (Long) type.get("count");
            typeMap.put(name, count);
        }
        return typeMap;
    }
}
