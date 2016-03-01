##Android custom loading

A custom loading view containing layouts, raw(.gif) file and fragment

### how to use :

1. copy OAuthHeaderBuilder.java to utils package
2. copy SimpleDividerItemDecoration.java and AnimatedGifImageView.java to views package
3. insert your custom *.gif to raw package 
4. copy loader.xml to layout package
5. include loader.xml layout inside your fragment_layout
6. check for TODO tags inside MainFragment.java
7. rename / refactor / edit the code as you please

### dependencies:

compile 'com.jakewharton:butterknife:7.0.1'<br/>
compile 'com.koushikdutta.ion:ion:2.+'<br/>
compile 'com.google.code.gson:gson:2.5'<br/>
compile(group: 'oauth.signpost', name: 'signpost-core', version: '1.2.1.2')<br/>
compile(group: 'oauth.signpost', name: 'signpost-commonshttp4', version: '1.2.1.2')<br/>
