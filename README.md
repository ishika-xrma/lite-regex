# lite-regex
LiteRegex is a simplified regular expression engine implemented purely in Java. Unlike traditional regex engines that convert patterns to DFAs (Deterministic Finite Automata), LiteRegex uses only NFAs (Nondeterministic Finite Automata) for matching, making it:
  - Faster to compile patterns (no DFA conversion step)
  - More memory efficient for complex patterns
  - Simpler to understand and modify

Features
- ✅ Basic regex operations: *, +, ?, |, [], {}
- ✅ Character classes: \d, \w, .
- ✅ Grouping with ()
- ✅ Quantifiers: {n}, {n,m}, {n,}
- ✅ Length analysis optimizations
- ✅ Pattern explanation system
- ✅ Both CLI and GUI interfaces
  
Performance Characteristics
  - Faster pattern compilation than DFA-based engines
  - Slower matching for complex patterns (tradeoff for simpler implementation)
  - More predictable memory usage (no DFA state explosion)
