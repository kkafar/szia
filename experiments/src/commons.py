COL_TIME = 'time'
COL_ID = 'roomid'
COL_INIT_ENERGY = 'init_energy'
COL_DEF_TEMP = 'default_temp'
COL_TARGET_TEMP = 'target_temp'
COL_POW_AV = 'pow_available'
COL_POW_CONS = 'pow_consumed'
COL_TEMP = 'temp'
COL_THERM_CAP = 'thermcap'
COL_THERM_RES = 'thermres'
COL_METRIC = 'metric'

GENERAL_DATA_COLUMNS = [
    COL_TIME,
    COL_THERM_CAP,
    COL_THERM_RES,
    COL_METRIC,
]

AGENT_DATA_COLUMNS = [
    COL_TIME,
    COL_ID,
    COL_INIT_ENERGY,
    COL_DEF_TEMP,
    COL_TARGET_TEMP,
    COL_POW_AV,
    COL_POW_CONS,
    COL_TEMP,
]

PRIMARY_MARK_STYLE = 'o'
SECONDARY_MARK_STYLE = '^'

PRIMARY_LINE_STYLE = '--'
SECONDARY_LINE_STYLE = ':'
