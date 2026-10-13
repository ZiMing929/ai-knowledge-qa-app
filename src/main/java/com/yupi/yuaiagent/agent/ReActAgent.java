package com.yupi.yuaiagent.agent;

import com.yupi.yuaiagent.agent.model.AgentState;
import lombok.EqualsAndHashCode;

/**
 * ReAct 智能体抽象类。
 *
 * ReAct = Reasoning + Acting，即思考 + 行动。
 *
 * 继承关系：
 * BaseAgent -> ReActAgent -> ToolCallAgent -> YuManus
 *
 * think()：判断下一步是否需要调用工具。
 * act()：执行大模型选择的工具。
 * step()：将思考和行动组合成一个执行步骤。
 */
@EqualsAndHashCode(callSuper = true)
public abstract class ReActAgent extends BaseAgent {

    /**
     * 思考阶段。
     *
     * @return true：需要调用工具；false：不需要调用工具
     */
    public abstract boolean think();

    /**
     * 行动阶段。
     *
     * @return 工具执行结果
     */
    public abstract String act();

    /**
     * 无需调用工具时返回的结果。
     *
     * 子类 ToolCallAgent 会重写此方法，
     * 返回大模型真正生成的文字。
     */
    protected String getNoActionResult() {
        return "思考完成，无需调用工具。";
    }

    /**
     * 执行一个完整的 ReAct 步骤。
     *
     * 执行顺序：
     * 1. think() 调用大模型。
     * 2. 如果模型选择工具，调用 act()。
     * 3. 如果模型不需要工具，直接返回文字答案。
     */
    @Override
    public String step() {
        try {
            // 判断当前步骤是否需要执行工具
            boolean shouldAct = think();

            // 无需工具：返回模型生成的文字
            if (!shouldAct) {
                return getNoActionResult();
            }

            // 需要工具：执行工具并返回结果
            return act();

        } catch (Exception e) {
            // 记录执行错误状态
            setState(AgentState.ERROR);

            // 保留原始异常，方便控制台定位
            throw new IllegalStateException("ReAct 步骤执行失败", e);
        }
    }
}
