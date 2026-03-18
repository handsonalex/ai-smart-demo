package com.smart.application.service;

import com.smart.common.enums.ConditionType;
import com.smart.common.enums.ValueSign;
import com.smart.domain.entity.SceneRule;
import com.smart.infrastructure.kafka.DeviceDataMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleEngineServiceTest {

    private RuleEngineService ruleEngineService;

    @BeforeEach
    void setUp() {
        ruleEngineService = new RuleEngineService();
    }

    @Test
    @DisplayName("空规则列表应返回 false")
    void matchEmptyRules() {
        DeviceDataMessage data = buildData();
        assertFalse(ruleEngineService.match(Collections.emptyList(), data));
        assertFalse(ruleEngineService.match(null, data));
    }

    @Test
    @DisplayName("SOC 大于阈值应匹配成功")
    void matchSocGt() {
        SceneRule rule = buildRule(ConditionType.SOC.getCode(), ValueSign.GT.getCode(), "50");
        DeviceDataMessage data = buildData();
        data.setSoc(new BigDecimal("80"));
        assertTrue(ruleEngineService.match(List.of(rule), data));
    }

    @Test
    @DisplayName("SOC 小于阈值应匹配失败")
    void matchSocGtFail() {
        SceneRule rule = buildRule(ConditionType.SOC.getCode(), ValueSign.GT.getCode(), "90");
        DeviceDataMessage data = buildData();
        data.setSoc(new BigDecimal("80"));
        assertFalse(ruleEngineService.match(List.of(rule), data));
    }

    @Test
    @DisplayName("功率小于等于阈值应匹配成功")
    void matchPowerLte() {
        SceneRule rule = buildRule(ConditionType.POWER.getCode(), ValueSign.LTE.getCode(), "100");
        DeviceDataMessage data = buildData();
        data.setPower(new BigDecimal("100"));
        assertTrue(ruleEngineService.match(List.of(rule), data));
    }

    @Test
    @DisplayName("多规则全部满足应匹配成功")
    void matchMultipleRulesAllPass() {
        SceneRule rule1 = buildRule(ConditionType.SOC.getCode(), ValueSign.GTE.getCode(), "50");
        SceneRule rule2 = buildRule(ConditionType.TEMPERATURE.getCode(), ValueSign.LT.getCode(), "45");
        DeviceDataMessage data = buildData();
        data.setSoc(new BigDecimal("60"));
        data.setTemperature(new BigDecimal("35"));
        assertTrue(ruleEngineService.match(List.of(rule1, rule2), data));
    }

    @Test
    @DisplayName("多规则部分不满足应匹配失败")
    void matchMultipleRulesPartialFail() {
        SceneRule rule1 = buildRule(ConditionType.SOC.getCode(), ValueSign.GTE.getCode(), "50");
        SceneRule rule2 = buildRule(ConditionType.TEMPERATURE.getCode(), ValueSign.LT.getCode(), "30");
        DeviceDataMessage data = buildData();
        data.setSoc(new BigDecimal("60"));
        data.setTemperature(new BigDecimal("35"));
        assertFalse(ruleEngineService.match(List.of(rule1, rule2), data));
    }

    @Test
    @DisplayName("电压等于阈值应匹配成功")
    void matchVoltageEq() {
        SceneRule rule = buildRule(ConditionType.VOLTAGE.getCode(), ValueSign.EQ.getCode(), "380");
        DeviceDataMessage data = buildData();
        data.setVoltage(new BigDecimal("380"));
        assertTrue(ruleEngineService.match(List.of(rule), data));
    }

    private SceneRule buildRule(int conditionType, int conditionSign, String threshold) {
        SceneRule rule = new SceneRule();
        rule.setConditionType(conditionType);
        rule.setConditionSign(conditionSign);
        rule.setThresholdValue(threshold);
        return rule;
    }

    private DeviceDataMessage buildData() {
        DeviceDataMessage data = new DeviceDataMessage();
        data.setDeviceId(1L);
        data.setStationId(1L);
        data.setPower(new BigDecimal("50"));
        data.setVoltage(new BigDecimal("380"));
        data.setCurrent(new BigDecimal("10"));
        data.setTemperature(new BigDecimal("30"));
        data.setSoc(new BigDecimal("70"));
        return data;
    }
}
