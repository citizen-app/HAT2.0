(this["webpackJsonprumpel-react"]=this["webpackJsonprumpel-react"]||[]).push([[4],{133:function(e,t,n){},134:function(e,t){var n=/[\'\"]/;e.exports=function(e){return e?(n.test(e.charAt(0))&&(e=e.substr(1)),n.test(e.charAt(e.length-1))&&(e=e.substr(0,e.length-1)),e):""}},135:function(e,t,n){},179:function(e,t,n){"use strict";n.r(t);var r=n(0),a=n.n(r),c=(n(133),n(50)),o=n(13),i=n(6),l=n(17),u=n(44),s=n(45),p=n(134),f=n.n(p),d=Object.assign||function(e){for(var t,n=1;n<arguments.length;n++)for(var r in t=arguments[n])Object.prototype.hasOwnProperty.call(t,r)&&(e[r]=t[r]);return e},m="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"==typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e};var g={accesskey:"accessKey",allowfullscreen:"allowFullScreen",allowtransparency:"allowTransparency",autocomplete:"autoComplete",autofocus:"autoFocus",autoplay:"autoPlay",cellpadding:"cellPadding",cellspacing:"cellSpacing",charset:"charSet",class:"className",classid:"classId",colspan:"colSpan",contenteditable:"contentEditable",contextmenu:"contextMenu",crossorigin:"crossOrigin",enctype:"encType",for:"htmlFor",formaction:"formAction",formenctype:"formEncType",formmethod:"formMethod",formnovalidate:"formNoValidate",formtarget:"formTarget",frameborder:"frameBorder",hreflang:"hrefLang",inputmode:"inputMode",keyparams:"keyParams",keytype:"keyType",marginheight:"marginHeight",marginwidth:"marginWidth",maxlength:"maxLength",mediagroup:"mediaGroup",minlength:"minLength",novalidate:"noValidate",radiogroup:"radioGroup",readonly:"readOnly",rowspan:"rowSpan",spellcheck:"spellCheck",srcdoc:"srcDoc",srclang:"srcLang",srcset:"srcSet",tabindex:"tabIndex",usemap:"useMap"},h={amp:"&",apos:"'",gt:">",lt:"<",nbsp:"\xa0",quot:"\u201c"},y=["style","script"],k=/([-A-Z0-9_:]+)(?:\s*=\s*(?:(?:"((?:\\.|[^"])*)")|(?:'((?:\\.|[^'])*)')|(?:\{((?:\\.|{[^}]*?}|[^}])*)\})))?/gi,v=/mailto:/i,b=/\n{2,}$/,x=/^( *>[^\n]+(\n[^\n]+)*\n*)+\n{2,}/,E=/^ *> ?/gm,w=/^ {2,}\n/,O=/^(?:( *[-*_]) *){3,}(?:\n *)+\n/,S=/^\s*(`{3,}|~{3,}) *(\S+)? *\n([\s\S]+?)\s*\1 *(?:\n *)+\n?/,N=/^(?: {4}[^\n]+\n*)+(?:\n *)+\n?/,j=/^(`+)\s*([\s\S]*?[^`])\s*\1(?!`)/,T=/^(?:\n *)*\n/,C=/\r\n?/g,$=/^\[\^(.*)\](:.*)\n/,A=/^\[\^(.*)\]/,L=/\f/g,_=/^\s*?\[(x|\s)\]/,z=/^ *(#{1,6}) *([^\n]+)\n{0,2}/,U=/^([^\n]+)\n *(=|-){3,} *(?:\n *)+\n/,B=/^ *(?!<[a-z][^ >/]* ?\/>)<([a-z][^ >/]*) ?([^>]*)\/{0}>\n?(\s*(?:<\1[^>]*?>[\s\S]*?<\/\1>|(?!<\1)[\s\S])*?)<\/\1>\n*/i,I=/&([a-z]+);/g,P=/^<!--.*?-->/,R=/^(data|aria|x)-[a-z_][a-z\d_.-]*$/,M=/^ *<([a-z][a-z0-9:]*)(?:\s+((?:<.*?>|[^>])*))?\/?>(?!<\/\1>)(\s*\n)?/i,D=/^\{.*\}$/,F=/^(https?:\/\/[^\s<]+[^<.,:;"')\]\s])/,Z=/^<([^ >]+@[^ >]+)>/,q=/^<([^ >]+:\/[^ >]+)>/,G=/ *\n+$/,H=/(?:^|\n)( *)$/,J=/-([a-z])?/gi,V=/^(.*\|?.*)\n *(\|? *[-:]+ *\|[-| :]*)\n((?:.*\|.*\n)*)\n?/,K=/^((?:[^\n]|\n(?! *\n))+)(?:\n *)+\n/,Q=/^\[([^\]]*)\]:\s*(\S+)\s*("([^"]*)")?/,W=/^!\[([^\]]*)\] ?\[([^\]]*)\]/,X=/^\[([^\]]*)\] ?\[([^\]]*)\]/,Y=/(\[|\])/g,ee=/(\n|^[-*]\s|^#|^ {2,}|^-{2,}|^>\s)/,te=/\t/g,ne=/^ *\| */,re=/(^ *\||\| *$)/g,ae=/ *$/,ce=/^ *:-+: *$/,oe=/^ *:-+ *$/,ie=/^ *-+: *$/,le=/^([*_])\1((?:\[.*?\][([].*?[)\]]|<.*?>(?:.*?<.*?>)?|`.*?`|~+.*?~+|.)*?)\1\1(?!\1)/,ue=/^([*_])((?:\[.*?\][([].*?[)\]]|<.*?>(?:.*?<.*?>)?|`.*?`|~+.*?~+|.)*?)\1(?!\1)/,se=/^~~((?:\[.*?\]|<.*?>(?:.*?<.*?>)?|`.*?`|.)*?)~~/,pe=/^\\([^0-9A-Za-z\s])/,fe=/^[\s\S]+?(?=[^0-9A-Z\s\u00c0-\uffff&;.()'"]|\d+\.|\n\n| {2,}\n|\w+:\S|$)/i,de=/(^\n+|\n+$|\s+$)/g,me=/^([ \t]*)/,ge=/\\([^0-9A-Z\s])/gi,he=/^( *)((?:[*+-]|\d+\.)) +/,ye=/( *)((?:[*+-]|\d+\.)) +[^\n]*(?:\n(?!\1(?:[*+-]|\d+\.) )[^\n]*)*(\n|$)/gm,ke=/^( *)((?:[*+-]|\d+\.)) [\s\S]+?(?:\n{2,}(?! )(?!\1(?:[*+-]|\d+\.) (?!(?:[*+-]|\d+\.) ))\n*|\s*\n*$)/,ve=/^\[((?:\[[^\]]*\]|[^\[\]]|\](?=[^\[]*\]))*)\]\(\s*<?((?:[^\s\\]|\\.)*?)>?(?:\s+['"]([\s\S]*?)['"])?\s*\)/,be=/^!\[((?:\[[^\]]*\]|[^\[\]]|\](?=[^\[]*\]))*)\]\(\s*<?((?:[^\s\\]|\\.)*?)>?(?:\s+['"]([\s\S]*?)['"])?\s*\)/,xe=[x,N,S,z,U,B,P,M,ye,ke,V,K];function Ee(e){return e.replace(/[\xc0\xc1\xc2\xc3\xc4\xc5\xe0\xe1\xe2\xe3\xe4\xe5\xe6\xc6]/g,"a").replace(/[\xe7\xc7]/g,"c").replace(/[\xf0\xd0]/g,"d").replace(/[\xc8\xc9\xca\xcb\xe9\xe8\xea\xeb]/g,"e").replace(/[\xcf\xef\xce\xee\xcd\xed\xcc\xec]/g,"i").replace(/[\xd1\xf1]/g,"n").replace(/[\xf8\xd8\u0153\u0152\xd5\xf5\xd4\xf4\xd3\xf3\xd2\xf2]/g,"o").replace(/[\xdc\xfc\xdb\xfb\xda\xfa\xd9\xf9]/g,"u").replace(/[\u0178\xff\xdd\xfd]/g,"y").replace(/[^a-z0-9- ]/gi,"").replace(/ /gi,"-").toLowerCase()}function we(e){return ie.test(e)?"right":ce.test(e)?"center":oe.test(e)?"left":null}function Oe(e,t,n){var r=n.inTable;n.inTable=!0;var a=t(e.trim(),n);n.inTable=r;var c=[[]];return a.forEach((function(e,t){"tableSeparator"===e.type?0!==t&&t!==a.length-1&&c.push([]):("text"===e.type&&(null==a[t+1]||"tableSeparator"===a[t+1].type)&&(e.content=e.content.replace(ae,"")),c[c.length-1].push(e))})),c}function Se(e,t,n){n.inline=!0;var r=Oe(e[1],t,n),a=function(e){return e.replace(re,"").split("|").map(we)}(e[2]),c=function(e,t,n){return e.trim().split("\n").map((function(e){return Oe(e,t,n)}))}(e[3],t,n);return n.inline=!1,{align:a,cells:c,header:r,type:"table"}}function Ne(e,t){return null==e.align[t]?{}:{textAlign:e.align[t]}}function je(e){function t(r,a){for(var c=[],o="";r;)for(var i=0;i<n.length;){var l=n[i],u=e[l],s=u.match(r,a,o);if(s){var p=s[0];r=r.substring(p.length);var f=u.parse(s,t,a);null==f.type&&(f.type=l),c.push(f),o=p;break}i++}return c}var n=Object.keys(e);return n.sort((function(t,n){var r=e[t].order,a=e[n].order;return r===a?t<n?-1:1:r-a})),function(e,n){return t(function(e){return e.replace(C,"\n").replace(L,"").replace(te,"    ")}(e),n)}}function Te(e){return function(t,n){return n.inline?e.exec(t):null}}function Ce(e){return function(t,n){return n.inline||n.simple?e.exec(t):null}}function $e(e){return function(t,n){return n.inline||n.simple?null:e.exec(t)}}function Ae(e){return function(t){return e.exec(t)}}function Le(e){try{if(decodeURIComponent(e).match(/^\s*javascript:/i))return null}catch(t){return null}return e}function _e(e){return e.replace(ge,"$1")}function ze(e,t,n){var r=n.inline||!1,a=n.simple||!1;n.inline=!0,n.simple=!0;var c=e(t,n);return n.inline=r,n.simple=a,c}function Ue(e,t,n){var r=n.inline||!1,a=n.simple||!1;n.inline=!1,n.simple=!0;var c=e(t,n);return n.inline=r,n.simple=a,c}function Be(e,t,n){return n.inline=!1,e(t+"\n\n",n)}function Ie(e,t,n){return{content:ze(t,e[1],n)}}function Pe(){return{}}function Re(){return null}function Me(){for(var e=arguments.length,t=Array(e),n=0;n<e;n++)t[n]=arguments[n];return t.filter(Boolean).join(" ")}function De(e,t,n){for(var r=e,a=t.split(".");a.length&&void 0!==(r=r[a[0]]);)a.shift();return r||n}function Fe(e,t){var n=De(t,e);return n?"function"==typeof n||"object"===("undefined"==typeof n?"undefined":m(n))&&"render"in n?n:De(t,e+".component",e):e}function Ze(e,t){function n(e,n){for(var r=De(t.overrides,e+".props",{}),a=arguments.length,c=Array(a>2?a-2:0),i=2;i<a;i++)c[i-2]=arguments[i];return o.apply(void 0,[Fe(e,t.overrides),d({},n,r,{className:Me(n&&n.className,r.className)||void 0})].concat(c))}function r(e){var r=!1;t.forceInline?r=!0:!t.forceBlock&&(r=!1===ee.test(e));var a=p(s(r?e:e.replace(de,"")+"\n\n",{inline:r})),c=void 0;return a.length>1?c=n(r?"span":"div",{key:"outer"},a):1===a.length?"string"==typeof(c=a[0])&&(c=n("span",{key:"outer"},c)):c=n("span",{key:"outer"}),c}function c(e){var t=e.match(k);return t?t.reduce((function(e,t,n){var c=t.indexOf("=");if(-1!==c){var o=function(e){return-1!==e.indexOf("-")&&null===e.match(R)&&(e=e.replace(J,(function(e,t){return t.toUpperCase()}))),e}(t.slice(0,c)).trim(),i=f()(t.slice(c+1).trim()),l=g[o]||o,u=e[l]=function(e,t){return"style"===e?t.split(/;\s?/).reduce((function(e,t){var n=t.slice(0,t.indexOf(":")),r=n.replace(/(-[a-z])/g,(function(e){return e[1].toUpperCase()}));return e[r]=t.slice(n.length+1).trim(),e}),{}):"href"===e?Le(t):(t.match(D)&&(t=t.slice(1,t.length-1)),"true"===t||"false"!==t&&t)}(o,i);(B.test(u)||M.test(u))&&(e[l]=a.a.cloneElement(r(u.trim()),{key:n}))}else e[g[t]||t]=!0;return e}),{}):void 0}(t=t||{}).overrides=t.overrides||{},t.slugify=t.slugify||Ee,t.namedCodesToUnicode=t.namedCodesToUnicode?d({},h,t.namedCodesToUnicode):h;var o=t.createElement||a.a.createElement;var i=[],l={},u={blockQuote:{match:$e(x),order:2,parse:function(e,t,n){return{content:t(e[0].replace(E,""),n)}},react:function(e,t,r){return n("blockquote",{key:r.key},t(e.content,r))}},breakLine:{match:Ae(w),order:2,parse:Pe,react:function(e,t,r){return n("br",{key:r.key})}},breakThematic:{match:$e(O),order:2,parse:Pe,react:function(e,t,r){return n("hr",{key:r.key})}},codeBlock:{match:$e(N),order:1,parse:function(e){return{content:e[0].replace(/^ {4}/gm,"").replace(/\n+$/,""),lang:void 0}},react:function(e,t,r){return n("pre",{key:r.key},n("code",{className:e.lang?"lang-"+e.lang:""},e.content))}},codeFenced:{match:$e(S),order:1,parse:function(e){return{content:e[3],lang:e[2]||void 0,type:"codeBlock"}}},codeInline:{match:Ce(j),order:4,parse:function(e){return{content:e[2]}},react:function(e,t,r){return n("code",{key:r.key},e.content)}},footnote:{match:$e($),order:1,parse:function(e){return i.push({footnote:e[2],identifier:e[1]}),{}},react:Re},footnoteReference:{match:Te(A),order:2,parse:function(e){return{content:e[1],target:"#"+e[1]}},react:function(e,t,r){return n("a",{key:r.key,href:Le(e.target)},n("sup",{key:r.key},e.content))}},gfmTask:{match:Te(_),order:2,parse:function(e){return{completed:"x"===e[1].toLowerCase()}},react:function(e,t,r){return n("input",{checked:e.completed,key:r.key,readOnly:!0,type:"checkbox"})}},heading:{match:$e(z),order:2,parse:function(e,n,r){return{content:ze(n,e[2],r),id:t.slugify(e[2]),level:e[1].length}},react:function(e,t,r){return n("h"+e.level,{id:e.id,key:r.key},t(e.content,r))}},headingSetext:{match:$e(U),order:1,parse:function(e,t,n){return{content:ze(t,e[1],n),level:"="===e[2]?1:2,type:"heading"}}},htmlComment:{match:Ae(P),order:2,parse:function(){return{}},react:Re},image:{match:Ce(be),order:2,parse:function(e){return{alt:e[1],target:_e(e[2]),title:e[3]}},react:function(e,t,r){return n("img",{key:r.key,alt:e.alt||void 0,title:e.title||void 0,src:Le(e.target)})}},link:{match:Te(ve),order:4,parse:function(e,t,n){return{content:Ue(t,e[1],n),target:_e(e[2]),title:e[3]}},react:function(e,t,r){return n("a",{key:r.key,href:Le(e.target),title:e.title},t(e.content,r))}},linkAngleBraceStyleDetector:{match:Te(q),order:1,parse:function(e){return{content:[{content:e[1],type:"text"}],target:e[1],type:"link"}}},linkBareUrlDetector:{match:Te(F),order:1,parse:function(e){return{content:[{content:e[1],type:"text"}],target:e[1],title:void 0,type:"link"}}},linkMailtoDetector:{match:Te(Z),order:1,parse:function(e){var t=e[1],n=e[1];return v.test(n)||(n="mailto:"+n),{content:[{content:t.replace("mailto:",""),type:"text"}],target:n,type:"link"}}},list:{match:function(e,t,n){var r=H.exec(n),a=t._list||!t.inline;return r&&a?(e=r[1]+e,ke.exec(e)):null},order:2,parse:function(e,t,n){var r=e[2],a=r.length>1,c=a?+r:void 0,o=e[0].replace(b,"\n").match(ye),i=!1;return{items:o.map((function(e,r){var a=he.exec(e)[0].length,c=new RegExp("^ {1,"+a+"}","gm"),l=e.replace(c,"").replace(he,""),u=r===o.length-1,s=-1!==l.indexOf("\n\n")||u&&i;i=s;var p,f=n.inline,d=n._list;n._list=!0,s?(n.inline=!1,p=l.replace(G,"\n\n")):(n.inline=!0,p=l.replace(G,""));var m=t(p,n);return n.inline=f,n._list=d,m})),ordered:a,start:c}},react:function(e,t,r){return n(e.ordered?"ol":"ul",{key:r.key,start:e.start},e.items.map((function(e,a){return n("li",{key:a},t(e,r))})))}},newlineCoalescer:{match:$e(T),order:4,parse:Pe,react:function(){return"\n"}},paragraph:{match:$e(K),order:4,parse:Ie,react:function(e,t,r){return n("p",{key:r.key},t(e.content,r))}},ref:{match:Te(Q),order:1,parse:function(e){return l[e[1]]={target:e[2],title:e[4]},{}},react:Re},refImage:{match:Ce(W),order:1,parse:function(e){return{alt:e[1]||void 0,ref:e[2]}},react:function(e,t,r){return n("img",{key:r.key,alt:e.alt,src:Le(l[e.ref].target),title:l[e.ref].title})}},refLink:{match:Te(X),order:1,parse:function(e,t,n){return{content:t(e[1],n),fallbackContent:t(e[0].replace(Y,"\\$1"),n),ref:e[2]}},react:function(e,t,r){return l[e.ref]?n("a",{key:r.key,href:Le(l[e.ref].target),title:l[e.ref].title},t(e.content,r)):n("span",{key:r.key},t(e.fallbackContent,r))}},table:{match:$e(V),order:2,parse:Se,react:function(e,t,r){return n("table",{key:r.key},n("thead",null,n("tr",null,e.header.map((function(a,c){return n("th",{key:c,style:Ne(e,c)},t(a,r))})))),n("tbody",null,e.cells.map((function(a,c){return n("tr",{key:c},a.map((function(a,c){return n("td",{key:c,style:Ne(e,c)},t(a,r))})))}))))}},tableSeparator:{match:function(e,t){return t.inTable?ne.exec(e):null},order:2,parse:function(){return{type:"tableSeparator"}},react:function(){return" | "}},text:{match:Ae(fe),order:5,parse:function(e){return{content:e[0].replace(I,(function(e,n){return t.namedCodesToUnicode[n]?t.namedCodesToUnicode[n]:e}))}},react:function(e){return e.content}},textBolded:{match:Ce(le),order:3,parse:function(e,t,n){return{content:t(e[2],n)}},react:function(e,t,r){return n("strong",{key:r.key},t(e.content,r))}},textEmphasized:{match:Ce(ue),order:4,parse:function(e,t,n){return{content:t(e[2],n)}},react:function(e,t,r){return n("em",{key:r.key},t(e.content,r))}},textEscaped:{match:Ce(pe),order:2,parse:function(e){return{content:e[1],type:"text"}}},textStrikethroughed:{match:Ce(se),order:4,parse:Ie,react:function(e,t,r){return n("del",{key:r.key},t(e.content,r))}}};!0!==t.disableParsingRawHTML&&(u.htmlBlock={match:Ae(B),order:2,parse:function(e,t,n){var r=e[3].match(me)[1],a=new RegExp("^"+r,"gm"),o=e[3].replace(a,""),i=function(e){return xe.some((function(t){return t.test(e)}))}(o)?Be:ze,l=e[1].toLowerCase(),u=-1!==y.indexOf(l);return{attrs:c(e[2]),content:u?e[3]:i(t,o,n),noInnerParse:u,tag:u?l:e[1]}},react:function(e,t,r){return n(e.tag,d({key:r.key},e.attrs),e.noInnerParse?e.content:t(e.content,r))}},u.htmlSelfClosing={match:Ae(M),order:2,parse:function(e){return{attrs:c(e[2]||""),tag:e[1]}},react:function(e,t,r){return n(e.tag,d({},e.attrs,{key:r.key}))}});var s=je(u),p=function(e){return function t(n,r){if(r=r||{},Array.isArray(n)){for(var a=r.key,c=[],o=!1,i=0;i<n.length;i++){r.key=i;var l=t(n[i],r),u="string"==typeof l;u&&o?c[c.length-1]+=l:c.push(l),o=u}return r.key=a,c}return e(n,t,r)}}(function(e){return function(t,n,r){return e[t.type].react(t,n,r)}}(u)),m=r(function(e){return e.replace(/<!--[\s\S]*?(?:-->)/g,"")}(e));return i.length&&m.props.children.push(n("footer",{key:"footer"},i.map((function(e){return n("div",{id:e.identifier,key:e.identifier},e.identifier,p(s(e.footnote,{inline:!0})))})))),m}function qe(e){var t=e.children,n=e.options,r=function(e,t){var n={};for(var r in e)t.indexOf(r)>=0||Object.prototype.hasOwnProperty.call(e,r)&&(n[r]=e[r]);return n}(e,["children","options"]);return a.a.cloneElement(Ze(t,n),r)}n(135);var Ge=function(e){var t=e.app;return t.info.updateNotes?a.a.createElement("div",{className:"content-wrapper flex-column-wrapper flex-align-items-center"},a.a.createElement("section",{className:"title-section"},a.a.createElement("div",{className:"app-logo-wrapper"},a.a.createElement("img",{src:t.info.graphics.logo.normal,className:"app-logo",height:"100",width:"100",alt:"".concat(t.info.name," logo")})),"App"===t.kind.kind&&a.a.createElement("div",{className:"app-rating-wrapper"},a.a.createElement("div",{className:"app-rating"},a.a.createElement("span",{className:"app-rating-highlighted"},t.info.rating.score,"\xa0"))),a.a.createElement("h3",{className:"title-section-heading"},t.info.name),a.a.createElement("div",{className:"title-section-text"},a.a.createElement(qe,null,t.info.updateNotes.header))),a.a.createElement("section",{className:"update-notes-section"},a.a.createElement("h4",{className:"section-header"},"Summary of updates"),t.info.updateNotes.notes&&a.a.createElement("ul",{className:"app-update-notes"},t.info.updateNotes.notes.map((function(e,t){return a.a.createElement("li",{className:"app-update-notes-item",key:t},a.a.createElement("div",{className:"app-update-notes-item-content"},a.a.createElement(qe,null,e)))}))))):null},He=n(94),Je=n(120),Ve=n(118),Ke=n(91);t.default=function(){var e=Object(o.c)(),t=Object(o.d)(u.d),n=Object(o.d)(l.c),p=Object(o.d)(c.b),f=Object(Ke.a)("application_id")||Object(Ke.a)("name"),d=null===f||void 0===f?void 0:f.toLowerCase(),m=Object(Ke.a)("redirect_uri")||Object(Ke.a)("redirect");Object(r.useEffect)((function(){e(d&&m?Object(u.b)():Object(c.c)("ERROR: App details incorrect. Please contact the app developer and let them know."))}),[]);return Object(r.useEffect)((function(){var e;n&&n.enabled&&!n.needsUpdating&&(e=n.application.id,"true"===Object(Ke.a)("internal")?m&&(window.location.href=m):i.a.getInstance().appLogin(e).then((function(e){if(e&&e.parsedBody){var t=e.parsedBody.accessToken,n="".concat(m).concat((null===m||void 0===m?void 0:m.includes("?"))?"&":"?","token=").concat(t);window.location.href=n.replace(/#/gi,"%23")}})))}),[n]),Object(r.useEffect)((function(){if(t&&t.length>0){var n=Object(Ke.a)("application_id")||Object(Ke.a)("name"),r=null===n||void 0===n?void 0:n.toLowerCase(),a=t.find((function(e){return e.application.id===r}));if(!a){var c=Object(Ke.a)("fallback");return i.a.getInstance().logout(),void(c&&(window.location.href=c))}e(Object(l.e)(a))}}),[t,e]),p?a.a.createElement("div",null,a.a.createElement("div",{className:"app-error"},a.a.createElement("h3",{className:"app-error-header"},"Ooops... Looks like something went wrong."),a.a.createElement("p",{className:"app-error-text"},p))):!n||n.enabled&&!n.needsUpdating?a.a.createElement(s.a,{loadingText:"Loading permissions"}):a.a.createElement("div",null,n&&n.needsUpdating&&n.application.info.updateNotes?a.a.createElement(Ge,{app:n.application}):a.a.createElement(Je.a,{hmiType:He.a.baas}),a.a.createElement(Ve.a,{registrationType:He.a.baas,nextStep:function(){d&&e(Object(c.d)(d))},cancelStep:function(){return function(){var e=Object(Ke.a)("fallback");i.a.getInstance().logout(),e&&(window.location.href=e)}()}}))}}}]);
//# sourceMappingURL=hat_login.c06e8032.chunk.js.map