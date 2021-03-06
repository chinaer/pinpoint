/*
 * Copyright 2015 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.context;

import com.google.inject.Inject;

/**
 * @author HyunGil Jeong
 */
public class DefaultTransactionCounter implements TransactionCounter {

    private final IdGenerator idGenerator;

    @Inject
    public DefaultTransactionCounter(IdGenerator idGenerator) {
        if (idGenerator == null) {
            throw new NullPointerException("idGenerator cannot be null");
        }
        this.idGenerator = idGenerator;
    }
    
    @Override
    public long getTransactionCount(SamplingType samplingType) {
        // overflow improbable
        switch (samplingType) {
        case SAMPLED_NEW:
            return idGenerator.currentTransactionId() - AtomicIdGenerator.INITIAL_TRANSACTION_ID;
        case SAMPLED_CONTINUATION:
            return Math.abs(idGenerator.currentContinuedTransactionId() - AtomicIdGenerator.INITIAL_CONTINUED_TRANSACTION_ID) / AtomicIdGenerator.DECREMENT_CYCLE;
        case UNSAMPLED_NEW:
            return Math.abs(idGenerator.currentDisabledId() - AtomicIdGenerator.INITIAL_DISABLED_ID) / AtomicIdGenerator.DECREMENT_CYCLE;
        case UNSAMPLED_CONTINUATION:
            return Math.abs(idGenerator.currentContinuedDisabledId() - AtomicIdGenerator.INITIAL_CONTINUED_DISABLED_ID) / AtomicIdGenerator.DECREMENT_CYCLE;
        default:
            return 0L;
        }
    }

    @Override
    public long getTotalTransactionCount() {
        long count = 0L;
        for (SamplingType samplingType : SamplingType.values()) {
            count += getTransactionCount(samplingType);
        }
        return count;
    }
}
