package com.example.whoami.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class KakaoAccount {
    @JsonProperty("profile_needs_agreement")
    private boolean profileNeedsAgreement;

    @JsonProperty("profile_nickname_needs_agreement")
    private boolean profileNicknameNeedsAgreement;

    @JsonProperty("profile_image_needs_agreement")
    private boolean profileImageNeedsAgreement;

    @JsonProperty("profile")
    private Profile profile;

    @JsonProperty("name_needs_agreement")
    private boolean nameNeedsAgreement;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email_needs_agreement")
    private boolean emailNeedsAgreement;

    @JsonProperty("is_email_valid")
    private boolean isEmailValid;

    @JsonProperty("is_email_verified")
    private boolean isEmailVerified;

    @JsonProperty("email")
    private String email;

    @JsonProperty("age_range_needs_agreement")
    private boolean ageRangeNeedsAgreement;

    @JsonProperty("age_range")
    private String ageRange;

    @JsonProperty("birthyear_needs_agreement")
    private boolean birthyearNeedsAgreement;

    @JsonProperty("birthyear")
    private String birthyear;

    @JsonProperty("birthday_needs_agreement")
    private boolean birthdayNeedsAgreement;

    @JsonProperty("birthday")
    private String birthday;

    @JsonProperty("birthday_type")
    private String birthdayType;

    @JsonProperty("gender_needs_agreement")
    private boolean genderNeedsAgreement;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("phone_number_needs_agreement")
    private boolean phoneNumberNeedsAgreement;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("ci_needs_agreement")
    private boolean ciNeedsAgreement;

    @JsonProperty("ci")
    private String ci;

    @JsonProperty("ci_authenticated_at")
    private LocalDateTime ciAuthenticatedAt;

    // 문서에는 없음
    @JsonProperty("has_age_range")
    private boolean hasAgeRange;

    @JsonProperty("has_gender")
    private boolean hasGender;
}
