const platform = new H.service.Platform({
    'apikey': 'YOUR_APIKEY'
});

const defaultLayers = platform.createDefaultLayers();

const map = new H.Map(
    document.getElementById('mapContainer'), 
    defaultLayers.vector.normal.map,
    {
        zoom: 10,
        center: { lat: 40.6, lng: 15.2 }
});

var mapEvents = new H.mapevents.MapEvents(map);
var behavior = new H.mapevents.Behavior(mapEvents);

const service = platform.getSearchService();
const icon = new H.map.Icon('https://www.flaticon.com/svg/static/icons/svg/426/426833.svg', {size: {w: 25, h: 25}});

$(document).ready(function() {

    $.ajax({
        url: "http://localhost:4000/graphql",
        contentType: "application/json",
        type:'POST',
        data: JSON.stringify({ 
            query:`{getAllFireLocation}`
        }),
        success: function(result) {
            for(const i in result.data.getAllFireLocation){
                service.geocode({
                    q: result.data.getAllFireLocation[i]
                }, (result) => {
                    result.items.forEach((item) => {
                        map.addObject(new H.map.Marker(item.position, { icon: icon }));
                    });
                }, alert);
            }
        }
    })    
})

const ui = H.ui.UI.createDefault(map, defaultLayers, 'it-IT');