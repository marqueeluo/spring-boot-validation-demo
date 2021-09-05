package com.luo.demo.validation.controller;

import com.luo.demo.validation.domain.dto.OrgDto;
import com.luo.demo.validation.domain.dto.UserDto;
import com.luo.demo.validation.domain.groups.Groups;
import com.luo.demo.validation.domain.param.UserParam;
import com.luo.demo.validation.domain.result.CommonResult;
import com.luo.demo.validation.domain.result.UserResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * 示例 - controller
 *
 * @author luohq
 * @date 2021-09-04 13:43
 */
@Controller
@Validated
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/user/detail")
    @ResponseBody
    public CommonResult<UserResult> getUser(@NotNull @Min(1) Long id) {
        log.info("get user, param: id={}", id);
        UserResult userResult = new UserResult();
        BeanUtils.copyProperties(this.buildUserDto(id), userResult);
        log.info("get user, result: {}", userResult);
        return CommonResult.successData(userResult);
    }

    @PostMapping("/user/add")
    @ResponseBody
    public CommonResult<UserResult> addUser(@Validated @RequestBody UserParam userParam) {
        log.info("add user, json param: {}", userParam);
        return CommonResult.success();
    }

    @PostMapping("/user/addForm")
    @ResponseBody
    public CommonResult<UserResult> addUserForm(@Validated UserParam userParam) {
        log.info("add user, form param: {}", userParam);
        return CommonResult.success();
    }


    @PostMapping("/user/update")
    @ResponseBody
    public CommonResult<UserResult> updateUser(@Validated({Groups.Update.class, Default.class}) @RequestBody UserParam userParam) {
        log.info("update user, json param: {}", userParam);
        return CommonResult.success();
    }


    private UserDto buildUserDto(Long id) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName("Tom-".concat(id.toString()));
        userDto.setMail("tom@meixing.com");
        userDto.setPhone("18888888888");
        userDto.setSex(1);
        userDto.setBirthDateStr("2000-01-01");
        userDto.setBirthLocalDate(LocalDate.of(2000, 1, 1));
        userDto.setRegisterLocalDatetime(LocalDateTime.of(2020, 9, 4, 12, 10, 22));
        userDto.setOrgs(this.buildOrgDtoList());
        return userDto;

    }

    private List<OrgDto> buildOrgDtoList() {
        return LongStream.range(1, 3).mapToObj(orgId -> {
            OrgDto orgResult = new OrgDto();
            orgResult.setOrgId(orgId);
            orgResult.setOrgName("Org-".concat(String.valueOf(orgId)));
            return orgResult;
        }).collect(Collectors.toList());
    }

}
