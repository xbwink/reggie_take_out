package com.xb.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.reggie.entity.Employee;
import com.xb.reggie.mapper.EmployeeMapper;
import com.xb.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author xb
 * @create 2022-12-07 19:57
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
