/* eslint-disable */
import request from '@/request';

/** getAnswerCountGroupByApp GET /api/app/static/answer_count_group_by_app */
export async function getAnswerCountGroupByAppUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseListAppAnswerCountDTO_>(
    '/api/app/static/answer_count_group_by_app',
    {
      method: 'GET',
      ...(options || {}),
    },
  );
}

/** getResultGroupByApp GET /api/app/static/result_group_by_app */
export async function getResultGroupByAppUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getResultGroupByAppUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseListAppAnswerResultCountDTO_>(
    '/api/app/static/result_group_by_app',
    {
      method: 'GET',
      params: {
        ...params,
      },
      ...(options || {}),
    },
  );
}
