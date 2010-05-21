package com.joestelmach.natty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.debug.BlankDebugEventListener;

public class ParseListener extends BlankDebugEventListener {
  
  private static final Map<String, String> INTERESTING_RULES;
  
  static {
    INTERESTING_RULES = new LinkedHashMap<String, String>();
    INTERESTING_RULES.put("global_date_prefix", "date prefix");
    INTERESTING_RULES.put("relative_date", "relative date");
    INTERESTING_RULES.put("relaxed_date", "relaxed date");
    INTERESTING_RULES.put("formal_date", "formal date");
    INTERESTING_RULES.put("explicit_date", "explicit date");
    INTERESTING_RULES.put("relaxed_day_of_month", "day");
    INTERESTING_RULES.put("relaxed_month", "month");
    INTERESTING_RULES.put("relaxed_year", "year");
    INTERESTING_RULES.put("formal_month_of_year", "month");
    INTERESTING_RULES.put("formal_day_of_month", "day");
    INTERESTING_RULES.put("formal_year", "year");
    INTERESTING_RULES.put("relative_prefix", "relative prefix");
    INTERESTING_RULES.put("implicit_prefix", "implicit prefix");
    INTERESTING_RULES.put("relative_suffix", "relative suffix");
    INTERESTING_RULES.put("relative_target", "relative target");
    INTERESTING_RULES.put("relative_date_span", "span");
    INTERESTING_RULES.put("relative_occurrence_index", "relative occurrence index");
    INTERESTING_RULES.put("named_relative_date", "named relative date");
    INTERESTING_RULES.put("day_of_week", "weekday");
    INTERESTING_RULES.put("date", "date");
    INTERESTING_RULES.put("date_time_alternative", "alternative");
    INTERESTING_RULES.put("alternative_direction", "direction");
    INTERESTING_RULES.put("hours", "hours");
    INTERESTING_RULES.put("minutes", "minutes");
    INTERESTING_RULES.put("meridian_indicator", "am/pm");
    INTERESTING_RULES.put("time_zone", "zone");
    INTERESTING_RULES.put("time", "time");
  }

  private int backtracking = 0;
  private Map<String, Stack<List<Token>>> _ruleMap;
  private List<ParseLocation> _locations;
  
  public ParseListener() {
    _ruleMap = new HashMap<String, Stack<List<Token>>>();
    _locations = new ArrayList<ParseLocation>();
  }
  
  public List<ParseLocation> getLocations() {
    return _locations;
  }
  
  /** Backtracking or cyclic DFA, don't want to add nodes to tree */
  public void enterDecision(int d) {
    backtracking++;
  }

  public void exitDecision(int i) {
    backtracking--;
  }
  
  public void enterRule(String filename, String ruleName) {
    if (backtracking > 0) return;
    
    Stack<List<Token>> tokenListStack = _ruleMap.get(ruleName);
    if(tokenListStack == null) {
      tokenListStack = new Stack<List<Token>>();
      _ruleMap.put(ruleName, tokenListStack);
    }
    
    tokenListStack.push(new ArrayList<Token>());
  }

  public void exitRule(String filename, String ruleName) {
    if (backtracking > 0) return;
    
    List<Token> tokenList = _ruleMap.get(ruleName).pop();
    if(tokenList.size() > 0 && INTERESTING_RULES.keySet().contains(ruleName)) { 
        
      StringBuilder builder = new StringBuilder();
      for(Token token:tokenList) {
        builder.append(token.getText());
      }
      String text = builder.toString();
      int start = tokenList.get(0).getCharPositionInLine();
      int end = start + text.length();
      
      ParseLocation location = new ParseLocation();
      location.setRuleName(INTERESTING_RULES.get(ruleName));
      location.setText(text);
      location.setStart(start);
      location.setEnd(end);
      _locations.add(location);
    }
  }

  public void consumeToken(Token token) {
    if (backtracking > 0) return;
    for(Stack<List<Token>> stack:_ruleMap.values()) {
      for(List<Token> tokenList:stack) {
        tokenList.add(token);
      }
    }
  }

  public void consumeHiddenToken(Token token) {
    if (backtracking > 0) return;
  }

  public void recognitionException(RecognitionException e) {
    if (backtracking > 0) return;
  }
  
}