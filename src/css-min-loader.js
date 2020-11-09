module.exports = function(source) {
	return source.split("\n")
		.map(line => line.trim())
		.filter(line => line.length > 0)
		.map(line => line.replace(/:\s+/g, ':'))
		.map(line => line.replace(/\s*([{},;])\s*/g, '$1'))
		.map(line => 
			!line.match(/[:,{};]$/)?
				(line + ';'):
				line
		)
		.join("");
}