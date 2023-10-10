package com.czsy.bean;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AnJianBean implements IBaseBean {
    public long id;
    public final List<String> pics = new LinkedList<>();
    public Map<Long, Integer> question_answer = new HashMap<>(); // 答案
}
