import { AxiosError } from "axios";

export type ApiResponse<T> = {
  data?  : T,
  error? : AxiosError
};

export type StatusResponse = {
	status?: number,
	error? : AxiosError
};
