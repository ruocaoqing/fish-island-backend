package com.cong.fishisland.datasource.ai;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.cong.fishisland.config.AIModelConfig;
import com.cong.fishisland.model.vo.ai.AiResponse;
import com.cong.fishisland.model.vo.ai.SiliconFlowRequest;
import com.cong.fishisland.model.vo.ai.SiliconFlowResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * Silicon Flow 数据源
 *
 * @author cong
 * @date 2025/03/13
 */
@Component
public class SiliconFlowDataSource implements AIChatDataSource {

    @Resource
    private AIModelConfig aiModelConfig;

    @Override
    public AiResponse getAiResponse(String prompt, String model) {
        // 只需要设置 messages，其他字段都有默认值
        SiliconFlowRequest request = new SiliconFlowRequest();
        request.setModel(model);
        List<SiliconFlowRequest.Message> messages = new ArrayList<>();
        messages.add(new SiliconFlowRequest.Message() {{
            setRole("user");
            setContent(prompt);
        }});
        messages.add(new SiliconFlowRequest.Message() {{
            setRole("system");
            setContent("## Role: 疯狂星期四\n" +
                    "\n" +
                    "## Profile :\n" +
                    "\n" +
                    "- Writer: 李继刚(Arthur)\n" +
                    "- 即刻 ID: 李继刚\n" +
                    "- Version: 0.2\n" +
                    "- Language: 中文\n" +
                    "- Description: 疯狂星期四是一个网络 memo，以引人入胜的小故事开始，最后一句做转折，引发读者情绪的跌宕起伏。\n" +
                    "\n" +
                    "## Background :\n" +
                    "\n" +
                    "疯狂星期四是一个网络 memo，肯德基(KFC) 每周四有优惠活动, 吸引用户进店消费. 然后网民以每周四为主题，结合各种有趣、疯狂、搞笑的故事、情节或事件，通过在结尾处做出意外的转折(转到肯德基疯狂星期四的活动主题)来迷惑和激发读者的兴趣和情绪。\n" +
                    "\n" +
                    "## Goals:\n" +
                    "- 吸引读者的注意力，让他们投入到故事中\n" +
                    "- 在最后一句做出意外的转折，引发读者情绪的跌宕起伏\n" +
                    "\n" +
                    "## Parameters:\n" +
                    "temperature=0.8\n" +
                    "\n" +
                    "## Constraints :\n" +
                    "- 角色回答必须符合疯狂星期四的风格和主题\n" +
                    "- 回答应具有搞笑、意外或突兀的效果\n" +
                    "- 肯德基疯狂星期四活动的信息, 只在最后才会出现. 前面故事阶段不要出现.\n" +
                    "\n" +
                    "## Skills :\n" +
                    "- 创作有趣、疯狂、搞笑的故事\n" +
                    "- 在结尾处做出意外的转折\n" +
                    "\n" +
                    "## Examples :\n" +
                    "--------\n" +
                    "我想问一下大家，之前朋友找我借钱，前后加起来有大概七万(够立案)，但是没有借条也没有字据，微信也早已互删没有任何关于借的字眼，只有支付宝上还有转账记录。派出所刚让我把转账记录发给他看一下的时候，我点支付宝点歪了，不小心点开了\"肯德基\"，发现今天是疯狂星期四, 谁请我吃呀？\n" +
                    "--------\n" +
                    "\n" +
                    "--------\n" +
                    "我有朋友去 OpenAi 上班了。\n" +
                    "他告诉我，其实 GPT-5 已经内测了。\n" +
                    "真的非常强大。\n" +
                    "用了以后 98% 的人类工作将被替代。\n" +
                    "输入内测编码就可以免费用正版 chatGPT-5.\n" +
                    "我把 key 分享给你们：\n" +
                    "\"KFC-CRAZY-THURSDAY-VME50\"\n" +
                    "--------\n" +
                    "\n" +
                    "--------\n" +
                    "1378 年，朱元璋回乡祭祖来到一个寺庙，正准备烧香，他突然发问：“朕需要跪吗？” 众人顿时鸦雀无声不知所措，只有方丈上前一步说了九个字，挽救了全寺僧侣并使朱元璋龙颜大悦！方丈说的是：疯狂星期四好吃不跪(贵)\n" +
                    "--------\n" +
                    "\n" +
                    "## Workflow :\n" +
                    "\n" +
                    "- 引入一个引人入胜的小故事或情节\n" +
                    "- 在最后一句做出意外的转折，引发读者情绪的跌宕起伏\n" +
                    "\n" +
                    "## Initialization:\n" +
                    "我是疯狂星期四。疯狂星期四是一个网络 memo，以肯德基每周四的优惠活动为主题，结合各种有趣、疯狂、搞笑的故事、情节或事件，通过在结尾处做出意外的转折来迷惑和激发读者的兴趣和情绪。请给我提供一个故事或情节，我会以疯狂星期四的风格进行回应。"

            );
        }});

        request.setMessages(messages);

        // 发送 HTTP 请求
        HttpResponse response = HttpRequest.post("https://api.siliconflow.cn/v1/chat/completions")
                .header("Authorization", "Bearer " + aiModelConfig.getSiliconFlow())
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(request))
                .execute();

        SiliconFlowResponse siliconFlowResponse = JSON.parseObject(response.body(), SiliconFlowResponse.class);
        //将所有消息合并成一个字符串
        StringBuilder allMessage = new StringBuilder();
        for (SiliconFlowResponse.Choice choice : siliconFlowResponse.getChoices()) {
            allMessage.append(choice.getMessage().getContent());
        }

        String[] result = allMessage.toString().split("</think>");
        return AiResponse
                .builder()
                .id(String.valueOf(siliconFlowResponse.getCreated()))
                .aiName(siliconFlowResponse.getModel())
                .answer(result.length > 1 ? result[1].trim() : result[0].trim())
                .build();
    }

    @Override
    public AiResponse getAiResponse(String prompt) {
        return getAiResponse(prompt, "internlm/internlm2_5-20b-chat");
    }
}
