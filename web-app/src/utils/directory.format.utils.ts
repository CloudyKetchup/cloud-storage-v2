export const formatSize = (size: number): string =>
{
	const sizeKb = 1024;
	const sizeMb = sizeKb * sizeKb;
	const sizeGb = sizeMb * sizeKb;
	const sizeTerra = sizeGb * sizeKb;

	let result;
	let ext;

	if (size < sizeMb)
	{
		result = `${size / sizeKb} `;
		ext = "Kb";
	} else if (size < sizeGb)
	{
		result = `${size / sizeMb} `;
		ext = "Mb";
	} else if (size < sizeTerra)
	{
		result = `${size / sizeGb} `;
		ext = "Gb";
	}

	return `${result?.substring(0, 3)} ${ext}`;
};