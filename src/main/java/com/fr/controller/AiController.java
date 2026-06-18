package com.fr.controller;

import com.fr.javaBean.Goods;
import com.fr.javaBean.Type;
import com.fr.service.GoodsService;
import com.fr.service.TypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/ai")
@CrossOrigin
public class AiController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private TypeService typeService;

    // ==================== 火山引擎 API 配置 ====================
    @Value("${ai.chat.api-key:}")
    private String apiKey;

    @Value("${ai.chat.model:ep-20260502165153-5zwf7}")
    private String model;

    @Value("${ai.chat.api-url:https://ark.cn-beijing.volces.com/api/v3/chat/completions}")
    private String apiUrl;
    
    // 从环境变量获取API密钥（优先级更高）
    private String getApiKey() {
        String envKey = System.getenv("ARK_API_KEY");
        if (envKey != null && !envKey.trim().isEmpty()) {
            return envKey;
        }
        return apiKey;
    }
    // ==================== 火山引擎 API 配置 ====================

    /**
     * 获取系统商品信息用于构建prompt
     */
    private String getGoodsInfo() {
        StringBuilder goodsInfo = new StringBuilder();
        goodsInfo.append("【蛋糕店商品信息】\n");
        goodsInfo.append("====================\n");

        QueryWrapper<Type> typeQuery = new QueryWrapper<>();
        List<Type> types = typeService.findTypes(typeQuery);

        for (Type type : types) {
            goodsInfo.append("【").append(type.getName()).append("】\n");
            
            QueryWrapper<Goods> goodsQuery = new QueryWrapper<>();
            goodsQuery.eq("type_id", type.getId());
            List<Goods> goodsList = goodsService.findGoods(goodsQuery);

            if (goodsList.isEmpty()) {
                goodsInfo.append("  暂无商品\n");
            } else {
                for (Goods goods : goodsList) {
                    goodsInfo.append("  • ").append(goods.getName())
                            .append(" - ¥").append(goods.getPrice())
                            .append(" (库存: ").append(goods.getStock()).append(")\n");
                }
            }
            goodsInfo.append("\n");
        }

        // 添加热门推荐
        QueryWrapper<Goods> hotQuery = new QueryWrapper<>();
        hotQuery.orderByDesc("stock");
        hotQuery.last("LIMIT 5");
        List<Goods> hotGoods = goodsService.findGoods(hotQuery);

        goodsInfo.append("【热门推荐】\n");
        for (Goods goods : hotGoods) {
            goodsInfo.append("  • ").append(goods.getName()).append("\n");
        }

        return goodsInfo.toString();
    }

    /**
     * 构建系统prompt
     */
    private String buildSystemPrompt() {
        String goodsInfo = getGoodsInfo();

        return "你是蛋糕店的AI客服小甜，负责回答顾客关于蛋糕店的各种问题。\n\n" +
                "你的职责：\n" +
                "1. 只能回答与本蛋糕店相关的问题\n" +
                "2. 如果顾客问有什么蛋糕，必须从下面的商品列表中选择回答\n" +
                "3. 回答要友好、亲切，使用可爱的表情符号\n" +
                "4. 如果问题与蛋糕店无关，请礼貌地拒绝回答\n" +
                "5. 价格和库存信息以商品列表为准\n\n" +
                "商品列表：\n" +
                "------------------------\n" +
                goodsInfo +
                "------------------------\n\n" +
                "请根据以上商品信息回答顾客的问题。";
    }

    /**
     * AI对话接口
     */
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String userMessage = request.get("message");
            
            // 构建请求体
            String jsonRequest = "{\n" +
                    "    \"model\": \"" + model + "\",\n" +
                    "    \"messages\": [\n" +
                    "        {\n" +
                    "            \"role\": \"system\",\n" +
                    "            \"content\": \"" + escapeJson(buildSystemPrompt()) + "\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"role\": \"user\",\n" +
                    "            \"content\": \"" + escapeJson(userMessage) + "\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"temperature\": 0.7,\n" +
                    "    \"max_tokens\": 1024\n" +
                    "}";

            // 发送请求
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + getApiKey());
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            // 读取响应
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder responseStr = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                responseStr.append(line);
            }
            br.close();

            // 解析响应
            String responseBody = responseStr.toString();
            String aiResponse = parseResponse(responseBody);

            response.put("success", true);
            response.put("message", aiResponse);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "抱歉，小甜暂时无法回答您的问题，请稍后再试~");
        }

        return response;
    }

    /**
     * 转义JSON字符串
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * 解析AI响应
     */
    private String parseResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            
            // 尝试从 choices 数组中获取内容
            JsonNode choicesNode = rootNode.get("choices");
            if (choicesNode != null && choicesNode.isArray() && !choicesNode.isEmpty()) {
                JsonNode firstChoice = choicesNode.get(0);
                JsonNode messageNode = firstChoice.get("message");
                if (messageNode != null) {
                    JsonNode contentNode = messageNode.get("content");
                    if (contentNode != null) {
                        return contentNode.asText();
                    }
                }
            }
            
            // 如果没有找到choices，尝试直接获取content字段
            JsonNode contentNode = rootNode.get("content");
            if (contentNode != null) {
                return contentNode.asText();
            }
            
            // 返回错误信息
            JsonNode errorNode = rootNode.get("error");
            if (errorNode != null) {
                JsonNode messageNode = errorNode.get("message");
                if (messageNode != null) {
                    return "错误：" + messageNode.asText();
                }
                return "API调用失败";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 如果都解析失败，返回原始响应（截取前200字符）
        if (response.length() > 200) {
            return response.substring(0, 200) + "...";
        }
        return response;
    }

    /**
     * 获取商品列表（用于前端展示）
     */
    @GetMapping("/goods")
    public Map<String, Object> getGoods() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            QueryWrapper<Goods> query = new QueryWrapper<>();
            List<Goods> goodsList = goodsService.findGoods(query);
            
            response.put("success", true);
            response.put("goods", goodsList);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取商品列表失败");
        }
        
        return response;
    }
    
    /**
     * 获取AI配置信息
     */
    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("success", true);
        response.put("apiKey", apiKey);
        response.put("model", model);
        response.put("apiUrl", apiUrl);
        
        return response;
    }
}