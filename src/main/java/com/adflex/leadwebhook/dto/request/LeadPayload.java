package com.adflex.leadwebhook.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeadPayload {

    @JsonProperty("ten_nguoi_gui")
    private String tenNguoiGui;

    @JsonProperty("sdt")
    private String sdt;

    @JsonProperty("email")
    private String email;

    @JsonProperty("dia_chi_dn")
    private String diaChiDn;

    @JsonProperty("ten_dn_option_1")
    private String tenDnOption1;

    @JsonProperty("ten_dn_option_2")
    private String tenDnOption2;

    @JsonProperty("ten_dn_option_3")
    private String tenDnOption3;

    @JsonProperty("ten_dn_option_4")
    private String tenDnOption4;

    @JsonProperty("ten_dn_option_5")
    private String tenDnOption5;

    @JsonProperty("nganh_nghe")
    private String nganhNghe;

    @JsonProperty("nhu_cau")
    private String nhuCau;

    // nếu MB Bank có gửi mã ref:
    @JsonProperty("mb_ref_id")
    private String mbRefId;

    // nếu có vốn điều lệ:
    @JsonProperty("charter_capital")
    private Long charterCapital;
}
