const {AppBar} = mui;
const Styles = mui.Styles;

App = React.createClass({
    childContextTypes: {
        muiTheme: React.PropTypes.object
    },

    getChildContext() {
        return {
            muiTheme: Styles.ThemeManager.getMuiTheme(Styles.LightRawTheme)
        };
    },

    render() {
        return (
            <div className="container">
                <AppBar
                    title="CRWD"
                    iconClassNameRight="muidocs-icon-navigation-expand-more"
                />

                <CrwdMap />
            </div>
        );
    }
});
