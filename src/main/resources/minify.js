/**
    Minify javascript code using UglifyJS2
    @see https://github.com/mishoo/UglifyJS2
    @param {String} code - The javascript code to minify
*/
function minify (code) {
    var ast = UglifyJS.parse(code);
    ast.figure_out_scope();
    compressor = UglifyJS.Compressor();
    ast = ast.transform(compressor);
    return ast.print_to_string();
}