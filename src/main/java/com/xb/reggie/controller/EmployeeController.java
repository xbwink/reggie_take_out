package com.xb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xb.reggie.common.R;
import com.xb.reggie.entity.Employee;
import com.xb.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 员工管理
 * @author xb
 * @create 2022-12-07 19:59
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1.将页面提交的密码进行md5加密处理
        String pwd = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());

        //2.根据页面提交的userName查询数据库
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(lqw);

        //3.如果没有查询到返回登录失败结果
        if (emp == null) {
            return R.error("登录失败");
        }

        //4.密码比对,不一致返回登录失败结果
        if (!pwd.equals(emp.getPassword())) {
            return R.error("登录失败");
        }

        //5.查看员工状态,如果为禁用状态 ,则返回员工已禁用
        if (emp.getStatus() == 0) {
            return R.error("该员工已禁用");
        }

        //6.登录成功,将员工id存入session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //1.清理session中的用户id
        request.getSession().removeAttribute("employee");
        //2.返回结果
        return R.success("退出成功");
    }

    /**
     * 添加员工
     *
     * @return
     */
    @PostMapping
    public R<String> addEmployee(HttpServletRequest request, @RequestBody Employee employee) {
        //设置默认密码为123456,需要进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //调用service层执行添加操作
        employeeService.save(employee);

        return R.success("添加员工成功");
    }

    /**
     * 员工信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name) {

        //创建分页构造器
        Page pageInfo = new Page(page, pageSize);

        //创建条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();

        //添加过滤条件
        lqw.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件(根据最近修改时间排序)
        lqw.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo, lqw);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工
     *
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        //执行修改
        employeeService.updateById(employee);

        return R.success("修改成功");
    }

    /**
     * 根据id查询返回员工
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable Long id){
        //调用service层执行查询
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应id的员工信息");
    }



}
