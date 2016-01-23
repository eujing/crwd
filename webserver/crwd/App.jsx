App = React.createClass({
    renderMap() {
        return <p>Map should be here or smthng</p>;
    },

    render() {
        return (
            <div className="container">
                <p>Hello World!</p>

                <div id="map">
                    {this.renderMap()}
                </div>
            </div>
        );
    }
});
