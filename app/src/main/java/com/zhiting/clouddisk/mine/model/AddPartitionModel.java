package com.zhiting.clouddisk.mine.model;

import com.zhiting.clouddisk.factory.ApiServiceFactory;
import com.zhiting.clouddisk.mine.contract.AddPartitionContract;
import com.zhiting.clouddisk.request.AddPartitionRequest;
import com.zhiting.clouddisk.request.ModifyPartitionRequest;
import com.zhiting.clouddisk.request.PoolNameRequest;
import com.zhiting.clouddisk.constant.Constant;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import io.reactivex.rxjava3.core.Observable;

public class AddPartitionModel extends AddPartitionContract.Model {

    public AddPartitionModel(){

    }

    /**
     * 添加 存储池分区
     * @param scopeToken
     * @param addPartitionRequest
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> addPartition(String scopeToken, AddPartitionRequest addPartitionRequest) {
        return ApiServiceFactory.getApiService().addPartition(scopeToken, addPartitionRequest);
    }

    /**
     * 编辑 存储池分区
     * @param scopeToken
     * @param modifyPartitionRequest
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> modifyPartition(String scopeToken, String name, ModifyPartitionRequest modifyPartitionRequest) {
        return ApiServiceFactory.getApiService().modifyPartition(scopeToken, name, modifyPartitionRequest);
    }

    /**
     * 删除 存储池分区
     * @param scopeToken
     * @param name
     * @return
     */
    @Override
    public Observable<BaseResponseEntity<Object>> removePartition(String scopeToken, String name, PoolNameRequest poolNameRequest) {
        return ApiServiceFactory.getApiService().removePartition(Constant.scope_token, name, poolNameRequest);
    }
}
