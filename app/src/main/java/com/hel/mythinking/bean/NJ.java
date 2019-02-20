package com.hel.mythinking.bean;

/**
 * @author hel
 * @date 2018/3/2
 * 文件 MyThinking
 * 描述
 */

public class NJ {
    public String answer;
    public String problem;
    public int id;

    public NJ(String answer, String problem) {
        this.answer = answer;
        this.problem = problem;
    }

    public NJ(String answer, String problem, int id) {
        this.answer = answer;
        this.problem = problem;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }
}
