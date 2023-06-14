import polars as pl


def general_data_file_name(experiment_id: int) -> str:
    return f'log-{experiment_id}.txt'


def agent_data_file_name(experiment_id: int) -> str:
    return f'agent-log-{experiment_id}.txt'


def extract_agent_ids(agent_df: pl.DataFrame) -> list[str]:
    return list(agent_df.get_column('roomid').unique())

