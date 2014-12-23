# UnfuckJs

UnfuckJs is IntelliJ WebStorm plugin for JavaScript reversing after it's been
minified/uglified. WebStorm already comes with lots of useful JavaScript
refactorings and quick fixes (intentions). Unfortunately they are designed to
fix typical programming mistakes and improve regular code. They don't help much
with JavaScript that has been put through a meat grinder.

At the moment the plugin is designed to work as a quick fix that is always
available in the Alt-Enter menu. If there's nothing to do it just doesn't. This
might be addressed in the future to improve user experience.

The plugin is trying to apply all fixes to the statement under cursor. The
plugin doesn't try to reimplement any fixes that are available in WebStorm, like
converting `?:` to `if/else`. Use those from the standard package.


## Quick Fixes

 - split comma expressions into individual statements
 - convert `&&` expressions to `if`
 - convert `||` expression to `if (! ...)`
 - strip unnecessary parenthesis in statements


## License

The library is released under [the MIT
license](http://www.opensource.org/licenses/mit-license.php).


## Copyright

Copyright (C) 2014 Dmitry Yakimenko (detunized@gmail.com)
