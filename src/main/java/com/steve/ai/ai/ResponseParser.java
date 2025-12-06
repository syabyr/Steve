package com.steve.ai.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.steve.ai.SteveMod;
import com.steve.ai.action.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseParser {
    
    public static ParsedResponse parseAIResponse(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }

        try {
            String jsonString = extractJSON(response);
            
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            
            String reasoning = json.has("reasoning") ? json.get("reasoning").getAsString() : "";
            String plan = json.has("plan") ? json.get("plan").getAsString() : "";
            List<Task> tasks = new ArrayList<>();
            
            if (json.has("tasks") && json.get("tasks").isJsonArray()) {
                JsonArray tasksArray = json.getAsJsonArray("tasks");
                
                for (JsonElement taskElement : tasksArray) {
                    if (taskElement.isJsonObject()) {
                        JsonObject taskObj = taskElement.getAsJsonObject();
                        Task task = parseTask(taskObj);
                        if (task != null) {
                            tasks.add(task);
                        }
                    }
                }
            }
            
            if (!reasoning.isEmpty()) {            }
            
            return new ParsedResponse(reasoning, plan, tasks);
            
        } catch (Exception e) {
            SteveMod.LOGGER.error("Failed to parse AI response: {}", response, e);
            return null;
        }
    }

    private static String extractJSON(String response) {
        String cleaned = response.trim();

        // If there's plain text和多个 JSON 块,优先截取包含 "\"tasks\"" 的最后一个 {...}
        int tasksIndex = cleaned.lastIndexOf("\"tasks\"");
        if (tasksIndex >= 0) {
            int openFromTasks = cleaned.lastIndexOf('{', tasksIndex);
            int globalLastBrace = cleaned.lastIndexOf('}');

            if (openFromTasks >= 0 && globalLastBrace > openFromTasks) {
                cleaned = cleaned.substring(openFromTasks, globalLastBrace + 1);
            }
        } else {
            // Fallback: take from first '{' to last '}'
            int firstBrace = cleaned.indexOf('{');
            int lastBrace = cleaned.lastIndexOf('}');
            if (firstBrace >= 0 && lastBrace > firstBrace) {
                cleaned = cleaned.substring(firstBrace, lastBrace + 1);
            }
        }

        cleaned = cleaned.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```") ) {
            cleaned = cleaned.substring(3);
        }

        if (cleaned.endsWith("```") ) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        cleaned = cleaned.trim();

        // Remove JavaScript-style comments the model might insert inside JSON
        // Line comments starting with //
        cleaned = cleaned.replaceAll("//.*?(?=\\n|$)", "");
        // Block comments like /* ... */
        cleaned = cleaned.replaceAll("/\\*.*?\\*/", "");

        // Fix common JSON formatting issues (collapse newlines)
        cleaned = cleaned.replaceAll("\\n\\s*", " ");

        // 简单括号平衡修复: 保证外层 { 和 } 数量一致
        int openCount = 0;
        int closeCount = 0;
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (c == '{') openCount++;
            else if (c == '}') closeCount++;
        }

        if (closeCount > openCount) {
            // 删除多余的尾部 '}'
            while (closeCount > openCount && cleaned.endsWith("}")) {
                cleaned = cleaned.substring(0, cleaned.length() - 1);
                closeCount--;
            }
        } else if (openCount > closeCount) {
            // 尾部补充缺失的 '}'
            StringBuilder sb = new StringBuilder(cleaned);
            while (openCount > closeCount) {
                sb.append('}');
                closeCount++;
            }
            cleaned = sb.toString();
        }

        return cleaned;
    }

    private static Task parseTask(JsonObject taskObj) {
        if (!taskObj.has("action")) {
            return null;
        }
        
        String action = taskObj.get("action").getAsString();
        Map<String, Object> parameters = new HashMap<>();
        
        if (taskObj.has("parameters") && taskObj.get("parameters").isJsonObject()) {
            JsonObject paramsObj = taskObj.getAsJsonObject("parameters");
            
            for (String key : paramsObj.keySet()) {
                JsonElement value = paramsObj.get(key);
                
                if (value.isJsonPrimitive()) {
                    if (value.getAsJsonPrimitive().isNumber()) {
                        parameters.put(key, value.getAsNumber());
                    } else if (value.getAsJsonPrimitive().isBoolean()) {
                        parameters.put(key, value.getAsBoolean());
                    } else {
                        parameters.put(key, value.getAsString());
                    }
                } else if (value.isJsonArray()) {
                    List<Object> list = new ArrayList<>();
                    for (JsonElement element : value.getAsJsonArray()) {
                        if (element.isJsonPrimitive()) {
                            if (element.getAsJsonPrimitive().isNumber()) {
                                list.add(element.getAsNumber());
                            } else {
                                list.add(element.getAsString());
                            }
                        }
                    }
                    parameters.put(key, list);
                }
            }
        }
        
        return new Task(action, parameters);
    }

    public static class ParsedResponse {
        private final String reasoning;
        private final String plan;
        private final List<Task> tasks;

        public ParsedResponse(String reasoning, String plan, List<Task> tasks) {
            this.reasoning = reasoning;
            this.plan = plan;
            this.tasks = tasks;
        }

        public String getReasoning() {
            return reasoning;
        }

        public String getPlan() {
            return plan;
        }

        public List<Task> getTasks() {
            return tasks;
        }
    }
}

