<!DOCTYPE html>
<meta charset="utf-8">
<style>
path {
  stroke: white;
  stroke-width: 0.25px;
  fill: grey;
}
</style>
<body>
<<<<<<< Updated upstream
<h2>Hello World 5!</h2>
=======
<!-- <script type="text/javascript" src="d3/d3.v3.js"></script> -->
<script type="text/javascript" src="http://d3js.org/d3.v3.js" charset="UTF-8"></script>
<!-- <script src="js/topojson.v0.min.js"></script> -->
<script src="http://d3js.org/topojson.v0.min.js"></script>
<script>

var width = 800,
    height = 600;

var projection = d3.geo.mercator()
    .center([100, 30 ])
    .rotate([-180,0]);

var svg = d3.select("body").append("svg")
    .attr("width", width)
    .attr("height", height);

var path = d3.geo.path()
    .projection(projection);

var g = svg.append("g");

// load and display the World
d3.json("json/world-110m2.json", function(error, topology) {
    g.selectAll("path")
      .data(topojson.object(topology, topology.objects.countries)
          .geometries)
    .enter()
      .append("path")
      .attr("d", path)
});

console.log()

d3.csv("cities.csv", function(error, data) {
        g.selectAll("circle")
           .data(data)
           .enter()
           .append("circle")
           .attr("cx", function(d) {
                   return projection([d.lon, d.lat])[0];
           })
           .attr("cy", function(d) {
                   return projection([d.lon, d.lat])[1];
           })
           .attr("r", 5)
           .style("fill", "red");
});

</script>
>>>>>>> Stashed changes
</body>
</html>
