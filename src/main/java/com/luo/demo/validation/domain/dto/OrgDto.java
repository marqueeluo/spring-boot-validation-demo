package com.luo.demo.validation.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * 组织 - DTO
 *
 * @author luohq
 * @date 2021-09-04 14:10
 */
public class OrgDto {
    @NotNull
    @Positive
    private Long orgId;

    @NotBlank
    @Size(min = 1, max = 32)
    private String orgName;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Override
    public String toString() {
        return "OrgDto{" +
                "orgId=" + orgId +
                ", orgName='" + orgName + '\'' +
                '}';
    }
}
