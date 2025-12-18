package com.xiaozhounandu.dto.response;

import com.xiaozhounandu.entity.Customer;
import com.xiaozhounandu.entity.FollowUp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetailResponse {
    private Customer customer;
    private List<FollowUp> followUps;
}
