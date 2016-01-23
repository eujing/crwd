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
        let el = ReactDOM.findDOMNode(this);
        let svg = d3.select(el)
                    .append("svg")
                    .attr("id", this.props.graphId)
                    .attr("width", this.props.width)
                    .attr("height", this.props.height);

        this.updateGraph(this.props);
    },

    componentWillUpdate(nextProps) {
        this.updateGraph(nextProps);
    },

    updateGraph(props) {
        let p = props;
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
            .x((d, i) => xMin(d[0]))
            .y((d, i) => yMin(d[1]))
            .interpolate("cardinal");

        let maxLine = d3.svg.line()
            .x((d, i) => xMax(d[0]))
            .y((d, i) => yMax(d[2]))
            .interpolate("cardinal");

        let currentLine = d3.svg.line()
            .x((d, i) => xCurrent(d[0]))
            .y((d, i) => yCurrent(d[1]))
            .interpolate("cardinal");

        let area = d3.svg.area()
            .x((d, i) => xMin(d[0]))
            .y0((d, i) => yMin(d[1]))
            .y1((d, i) => yMax(d[2]))
            .interpolate("cardinal");


        let svg = d3.select("#" + p.graphId)
                .attr("preserveAspectRatio", "xMinYMin meet")
                .attr("viewBox", "0 0 " + p.outerWidth + " " + p.outerHeight)
            .append("g")
                .attr("transform", "translate(" + p.margin.left + "," + p.margin.top + ")");

        svg.append("defs")
            .append("clipPath")
                .attr("id", "clip")
            .append("rect")
                .attr("width", width)
                .attr("height", height);

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
            .attr("d", area);
    },

    render() {
        return <div className="trend-graph"></div>;
    }
});
