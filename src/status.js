export const STATUS_DEFAULT = 0;
export const STATUS_NEAR = idx => idx+10;
export const STATUS_BOMB = 2;
export const STATUS_BOMB_END = 3;
export const STATUS_FLAGGED = 4;
export const STATUS_FLAGGED_FAILED = 5;

const minRevealedValue = STATUS_NEAR(0);
const maxRevealedValue = STATUS_NEAR(8);


export function statusClass(status) {
	const cov = 'cov ';
	if      (status == STATUS_DEFAULT)        return cov+'def';
	else if (status == STATUS_BOMB)           return cov+'b';
	else if (status == STATUS_BOMB_END)       return cov+'X';
	else if (status == STATUS_FLAGGED)        return cov+'f';
	else if (status == STATUS_FLAGGED_FAILED) return cov+'F';
	else /* (status == STATUS_NEAR) */        return `n${status-10}`;
}

export function statusRevealed(status) {
	return status >= minRevealedValue && status <= maxRevealedValue;
}