import polars as pl
import matplotlib.pyplot as plt
import sys
import utils
from pathlib import Path
from plot import plot_metric, plot_room
from commons import AGENT_DATA_COLUMNS, GENERAL_DATA_COLUMNS

pl.Config.set_tbl_cols(20)
pl.Config.set_tbl_rows(250)
plt.rcParams['figure.figsize'] = (16, 9)


if len(sys.argv) > 1:
    experiment_id = int(sys.argv[1])
else:
    experiment_id = 1

data_collections_dir_path = Path('data-collections')
plot_dir = Path('plots')

agent_data_file_path = data_collections_dir_path.joinpath(
    utils.agent_data_file_name(experiment_id))

general_data_file_path = data_collections_dir_path.joinpath(
    utils.general_data_file_name(experiment_id))

assert data_collections_dir_path.is_dir()
assert plot_dir.is_dir()
assert agent_data_file_path.is_file()
assert general_data_file_path.is_file()


raw_agent_df = pl.read_csv(agent_data_file_path, new_columns=AGENT_DATA_COLUMNS)
raw_general_df = pl.read_csv(general_data_file_path, new_columns=GENERAL_DATA_COLUMNS)

agent_ids = utils.extract_agent_ids(raw_agent_df)
print(f"Discovered agent ids: {agent_ids}")

# print(raw_agent_df)
# print(raw_general_df)


fig, plot = plt.subplots(nrows=1, ncols=1)
plot_metric(fig, plot, raw_general_df)
fig.tight_layout()

for agent_id in agent_ids:
    fig, plot = plt.subplots(nrows=2, ncols=2)
    plot_room(fig, plot[0][0], plot[0][1], plot[1][0], plot[1][1], raw_agent_df, agent_id)
    fig.tight_layout()


plt.show()

