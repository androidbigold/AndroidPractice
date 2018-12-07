$(document).ready(function () {
    stMenuTab = $("#tabs li");
    stMenuInfo= $("#pages > div");

    stMenuTab.click(function () {
        i = $(this).index();
        console.log(i);
        $(this).addClass('is-active').siblings().removeClass('is-active');
        stMenuInfo.eq(i).css("display", "");
        stMenuInfo.eq(i).siblings().css("display", "none");
    });
});

// window.onLoad  = function(){
//     map = new AMap.Map('map');
// };
// url = 'https://webapi.amap.com/maps?v=1.4.8&key=c10c539c32ffec90d88b633bff7a0443&callback=onLoad';
// jsapi = doc.createElement('script');
// jsapi.charset = 'utf-8';
// jsapi.src = url;
// document.head.appendChild(jsapi);