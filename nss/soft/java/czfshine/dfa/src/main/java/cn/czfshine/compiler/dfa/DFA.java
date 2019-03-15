package cn.czfshine.compiler.dfa;

import java.util.*;


public class DFA {

    /**
     * 状态
     */
    abstract class States {
        public String name;

        abstract States nextStates(String s) throws NotAcceptException;

        abstract void init();
    }

    private Map<String, States> startStatesSet = new HashMap<>();
    private Map<String, States> allStates = new HashMap<>();
    private Set<States> endStates = new HashSet<>();
    public DFA() {
        States nil = new States() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                if (s.equals("0")) {
                    return allStates.get("mod0");
                } else if (s.equals("1")) {
                    return allStates.get("mod1");
                } else {
                    throw new NotAcceptException();
                }
            }

            @Override
            void init() {
                name = "nil";
            }
        };

        States mod0 = new States() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                if (s.equals("1")) {
                    return allStates.get("mod1");
                } else if (s.equals("0")) {
                    return this;
                } else {
                    throw new NotAcceptException();
                }
            }

            @Override
            void init() {
                name = "mod0";
            }
        };

        States mod1 = new States() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                if (s.equals("1")) {
                    return allStates.get("mod0");
                } else if (s.equals("0")) {
                    return allStates.get("mod2");
                } else {
                    throw new NotAcceptException();
                }
            }

            @Override
            void init() {
                name = "mod1";
            }
        };
        States mod2 = new States() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                if (s.equals("0")) {
                    return allStates.get("mod1");
                } else if (s.equals("1")) {
                    return this;
                } else {
                    throw new NotAcceptException();
                }
            }

            @Override
            void init() {
                name = "mod2";
            }
        };

        allStates.put("nil", nil);
        allStates.put("mod0", mod0);
        allStates.put("mod1", mod1);
        allStates.put("mod2", mod2);

        startStatesSet.put("nil", nil);

        endStates.add(mod1);
    }

    private States getStartState() {
        return startStatesSet.get("nil");
    }

    public boolean Run(String str) throws NotAcceptException {
        States curState = getStartState();
        for (int i = 0; i < str.length(); i++) {
            String s = String.valueOf(str.charAt(i));
            curState = curState.nextStates(s);
        }
        if (endStates.contains(curState)) {
            return true;
        } else {
            return false;
        }
    }

    public static  void main(String[] atgv) {
        System.out.println("解析二进制串是否能被1整除");
        String str = "";

        while (true) {
            System.out.println("请输入一个二进制串，输入end结束");
            Scanner input = new Scanner(System.in);
            str=input.nextLine();
            if(str.equals("end")){
                break;
            }
            DFA dfa = new DFA();
            try {
                boolean res = dfa.Run(str);
                if (res) {
                    System.out.println("字符串接受");
                } else {
                    System.out.println("字符串不接受");
                }
            } catch (NotAcceptException e) {
                System.out.println("字符串不正确");
            }
        }


    }

    class NotAcceptException extends Exception {

    }



}
