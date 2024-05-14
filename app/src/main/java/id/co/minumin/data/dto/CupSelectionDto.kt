package id.co.minumin.data.dto

import id.co.minumin.const.MinuminConstant.CupSize.SIZE_100
import id.co.minumin.const.MinuminConstant.CupSize.SIZE_150
import id.co.minumin.const.MinuminConstant.CupSize.SIZE_200
import id.co.minumin.const.MinuminConstant.CupSize.SIZE_300
import id.co.minumin.const.MinuminConstant.CupSize.SIZE_400

/**
 * Created by pertadima on 11,February,2021
 */

enum class CupSelectionDto(val capacity: Int) {
    CUP_100(SIZE_100),
    CUP_150(SIZE_150),
    CUP_200(SIZE_200),
    CUP_300(SIZE_300),
    CUP_400(SIZE_400),
    CUP_CUSTOM(-1)
}