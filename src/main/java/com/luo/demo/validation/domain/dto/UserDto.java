package com.luo.demo.validation.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.luo.demo.validation.constraints.DateFormat;
import com.luo.demo.validation.constraints.DateTimeFormat;
import com.luo.demo.validation.constraints.IdNo;
import com.luo.demo.validation.constraints.PhoneNo;
import com.luo.demo.validation.domain.groups.Groups.Update;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户 - DTO
 *
 * @author luohq
 * @date 2021-09-04 13:45
 */
public class UserDto {

    @NotNull(groups = Update.class)
    @Positive
    private Long id;

    @NotBlank
    @Size(max = 32)
    private String name;

    @NotNull
    @Range(min = 1, max = 2)
    private Integer sex;

    @NotBlank
    //@Pattern(regexp = "^\\d{8,11}$")
    @PhoneNo
    private String phone;

    @NotNull
    @Email
    private String mail;

    @NotBlank
    @IdNo
    private String idNo;

    @NotNull
    //@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    @DateFormat
    //@DateTimeFormat
    private String birthDateStr;

    @NotNull
    @PastOrPresent
    private LocalDate birthLocalDate;

    @NotNull
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerLocalDatetime;

    @Valid
    @NotEmpty
    private List<OrgDto> orgs;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getBirthDateStr() {
        return birthDateStr;
    }

    public void setBirthDateStr(String birthDateStr) {
        this.birthDateStr = birthDateStr;
    }

    public LocalDate getBirthLocalDate() {
        return birthLocalDate;
    }

    public void setBirthLocalDate(LocalDate birthLocalDate) {
        this.birthLocalDate = birthLocalDate;
    }

    public LocalDateTime getRegisterLocalDatetime() {
        return registerLocalDatetime;
    }

    public void setRegisterLocalDatetime(LocalDateTime registerLocalDatetime) {
        this.registerLocalDatetime = registerLocalDatetime;
    }

    public List<OrgDto> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<OrgDto> orgs) {
        this.orgs = orgs;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", mail='" + mail + '\'' +
                ", idNo='" + idNo + '\'' +
                ", birthDateStr='" + birthDateStr + '\'' +
                ", birthLocalDate=" + birthLocalDate +
                ", registerLocalDatetime=" + registerLocalDatetime +
                ", orgs=" + orgs +
                '}';
    }
}
