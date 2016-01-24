TrendGraph = React.createClass({
    propTypes: {
        graphId: React.PropTypes.string.isRequired,
        rangeData: React.PropTypes.array.isRequired,
        trendData: React.PropTypes.array.isRequired,
        timeMin: React.PropTypes.number.isRequired,
        timeMax: React.PropTypes.number.isRequired,
        outerWidth: React.PropTypes.number.isRequired,
        outerHeight: React.PropTypes.number.isRequired,
        margin: React.PropTypes.object.isRequired
    },

    componentDidMount: function() {
        let p = this.props;
        let width = p.outerWidth - p.margin.left - p.margin.right;
        let height = p.outerHeight - p.margin.top - p.margin.bottom;

        let el = ReactDOM.findDOMNode(this);
        let svg = d3.select(el)
                    .append("svg")
                    .attr("id", this.props.graphId)
                    .attr("width", this.props.width)
                    .attr("height", this.props.height);

        svg.append("defs")
            .append("clipPath")
                .attr("id", "clip")
            .append("rect")
                .attr("width", width)
                .attr("height", height);
        
        this.xMin = d3.time.scale()
            .domain([p.timeMin, p.timeMax])
            .range([0, width]);

        this.yMin = d3.scale.linear()
            .domain([0, 1.0])
            .range([height, 0]);

        this.xMax = d3.time.scale()
            .domain([p.timeMin, p.timeMax])
            .range([0, width]);

        this.yMax = d3.scale.linear()
            .domain([0, 1.0])
            .range([height, 0]);

        this.xCurrent = d3.time.scale()
            .domain([p.timeMin, p.timeMax])
            .range([0, width]);

        this.yCurrent = d3.scale.linear()
            .domain([0, 1.0])
            .range([height, 0]);

        this.minLine = d3.svg.line()
            .x((d, i) => this.xMin(d.time))
            .y((d, i) => this.yMin(d.min))
            .interpolate("cardinal");

        this.maxLine = d3.svg.line()
            .x((d, i) => this.xMax(d.time))
            .y((d, i) => this.yMax(d.max))
            .interpolate("cardinal");

        this.currentLine = d3.svg.line()
            .x((d, i) => this.xCurrent(d.time))
            .y((d, i) => this.yCurrent(d.value))
            .interpolate("cardinal");

        this.area = d3.svg.area()
            .x((d, i) => this.xMin(d.time))
            .y0((d, i) => this.yMin(d.min))
            .y1((d, i) => this.yMax(d.max))
            .interpolate("cardinal");


        svg.attr("preserveAspectRatio", "xMinYMin meet")
                .attr("viewBox", "0 0 " + p.outerWidth + " " + p.outerHeight)
            .append("g")
                .attr("transform", "translate(" + p.margin.left + "," + p.margin.top + ")");

        
        this.xAxis = d3.svg.axis()
            .scale(this.xMin)
            .orient("bottom");

        this.yAxis = d3.svg.axis()
            .scale(this.yCurrent)
            .orient("left");

        svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(this.xAxis);

        svg.append("g")
            .attr("class", "y axis")
            .call(this.yAxis);

        let viewPort = svg.append("g")
            .attr("clip-path", "url(#clip)");

        let minLinePath = viewPort.append("path")
            .datum(p.rangeData)
            .attr("class", "min line")
            .attr("d", this.minLine);

        let maxLinePath = viewPort.append("path")
            .datum(p.rangeData)
            .attr("class", "max line")
            .attr("d", this.maxLine);

        let currentLinePath = viewPort.append("path")
            .datum(p.trendData)
            .attr("class", "current line")
            .attr("d", this.currentLine);

        let areaPath = viewPort.append("path")
            .datum(p.rangeData)
            .attr("class", "area")
            .attr("d", this.area);

        this.updateGraph(this.props);
    },

    componentWillUpdate(nextProps) {
        this.updateGraph(nextProps);
    },

    updateGraph(props) {
        /*let p = props;
        let width = p.outerWidth - p.margin.left - p.margin.right;
        let height = p.outerHeight - p.margin.top - p.margin.bottom;

        let xMin = d3.time.scale()
            .domain([p.timeMin, p.timeMax])
            .range([0, width]);

        let yMin = d3.scale.linear()
            .domain([0, 1.0])
            .range([height, 0]);

        let xMax = d3.time.scale()
            .domain([p.timeMin, p.timeMax])
            .range([0, width]);

        let yMax = d3.scale.linear()
            .domain([0, 1.0])
            .range([height, 0]);

        let xCurrent = d3.time.scale()
            .domain([p.timeMin, p.timeMax])
            .range([0, width]);

        let yCurrent = d3.scale.linear()
            .domain([0, 1.0])
            .range([height, 0]);

        let minLine = d3.svg.line()
            .x((d, i) => xMin(d.time))
            .y((d, i) => yMin(d.min))
            .interpolate("cardinal");

        let maxLine = d3.svg.line()
            .x((d, i) => xMax(d.time))
            .y((d, i) => yMax(d.max))
            .interpolate("cardinal");

        let currentLine = d3.svg.line()
            .x((d, i) => xCurrent(d.time))
            .y((d, i) => yCurrent(d.value))
            .interpolate("cardinal");

        let area = d3.svg.area()
            .x((d, i) => xMin(d.time))
            .y0((d, i) => yMin(d.min))
            .y1((d, i) => yMax(d.max))
            .interpolate("cardinal");


        let svg = d3.select("#" + p.graphId)
                .attr("preserveAspectRatio", "xMinYMin meet")
                .attr("viewBox", "0 0 " + p.outerWidth + " " + p.outerHeight)
            .append("g")
                .attr("transform", "translate(" + p.margin.left + "," + p.margin.top + ")");

        
        let xAxis = d3.svg.axis()
            .scale(xMin)
            .orient("bottom");

        let yAxis = d3.svg.axis()
            .scale(yCurrent)
            .orient("left");

        svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis);

        svg.append("g")
            .attr("class", "y axis")
            .call(yAxis);

        let viewPort = svg.append("g")
            .attr("clip-path", "url(#clip)");

        let minLinePath = viewPort.append("path")
            .datum(p.rangeData)
            .attr("class", "line")
            .attr("d", minLine);

        let maxLinePath = viewPort.append("path")
            .datum(p.rangeData)
            .attr("class", "line")
            .attr("d", maxLine);

        let currentLinePath = viewPort.append("path")
            .datum(p.trendData)
            .attr("class", "line")
            .attr("d", currentLine);

        let areaPath = viewPort.append("path")
            .datum(p.rangeData)
            .attr("class", "area")
            .attr("d", area);*/

        let svg = d3.select("#" + props.graphId).transition();

        this.xMin.domain([props.timeMin, props.timeMax]);
        this.yMin.domain([0, 1.0]);
        this.xMax.domain([props.timeMin, props.timeMax]);
        this.yMax.domain([0, 1.0]);
        this.xCurrent.domain([props.timeMin, props.timeMax]);
        this.yCurrent.domain([0, 1.0]);

        svg.select(".min.line")
            .duration(750)
            .ease("linear")
            .attr("d", this.minLine(props.rangeData));

        svg.select(".max.line")
            .duration(750)
            .ease("linear")
            .attr("d", this.maxLine(props.rangeData));

        svg.select(".current.line")
            .duration(750)
            .ease("linear")
            .attr("d", this.currentLine(props.trendData));

        svg.select(".area")
            .duration(750)
            .ease("linear")
            .attr("d", this.area(props.rangeData));

        svg.select(".x.axis")
            .duration(750)
            .ease("linear")
            .call(this.xAxis);

        svg.select(".y.axis")
            .duration(750)
            .ease("linear")
            .call(this.yAxis);
    },

    render() {
        return <div className="trend-graph"></div>;
    }
});
