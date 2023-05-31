import polars as pl
import matplotlib.pyplot as plt
import sys
from pathlib import Path

data_file = Path('data', 'log.txt')
if len(sys.argv) > 1:
    data_file = Path(sys.argv[1])

assert data_file.is_file(), "Data file must exist"

raw_data_df = pl.read_csv(data_file, new_columns=[
                          "time", "thermcap", "thermres", "metric"])


def plot_metric(raw_data: pl.DataFrame):
    fig, plot = plt.subplots(nrows=1, ncols=1)

    x_data = raw_data_df.get_column('time')
    y_data = raw_data_df.get_column('metric')
    plot.plot(x_data, y_data, linestyle='--')

    plot.set(
        title="Wartość metryki od ticku czasowego",
        xlabel="Tick czasowy",
        ylabel="Wartość metryki"
    )


plot_metric(raw_data_df)
plt.show()

