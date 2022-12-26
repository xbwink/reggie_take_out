package com.xb.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.reggie.entity.AddressBook;
import com.xb.reggie.mapper.AddressBookMapper;
import com.xb.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author xb
 * @create 2022-12-14 15:17
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
