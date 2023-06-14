import polars as pl
import matplotlib.pyplot as plt
from commons import (COL_ID, COL_TIME, COL_METRIC, COL_TEMP, COL_DEF_TEMP,
                     COL_TARGET_TEMP, PRIMARY_LINE_STYLE, PRIMARY_MARK_STYLE,
                     SECONDARY_MARK_STYLE, SECONDARY_LINE_STYLE, COL_POW_AV,
                     COL_POW_CONS)


def plot_metric(fig: plt.Figure, plot: plt.Axes, raw_data: pl.DataFrame):
    """ General data should be massed as raw_data """

    x_data = raw_data.get_column(COL_TIME)
    y_data = raw_data.get_column(COL_METRIC)
    plot.plot(x_data, y_data, linestyle=PRIMARY_LINE_STYLE,
              marker=PRIMARY_MARK_STYLE, label='Metryka')
    plot.plot(x_data, [0 for _ in range(len(y_data))],
              linestyle=SECONDARY_LINE_STYLE, label='Wartość idealna, $y = 0$')

    plot.set(
        title="Wartość metryki od ticku czasowego",
        xlabel="Tick czasowy",
        ylabel="Wartość metryki"
    )
    plot.legend()
    plot.grid()


def plot_room(fig: plt.Figure, plot_temp: plt.Axes, plot_diff: plt.Axes,
              plot_pa: plt.Axes, plot_pc: plt.Axes, agent_data: pl.DataFrame, roomid: str):
    agent_data = agent_data.filter(pl.col(COL_ID) == roomid).sort(COL_TIME)

    fig.suptitle(f'Analiza dla agenta "{roomid}"')
    plot_room_temp(fig, plot_temp, agent_data, roomid)
    plot_room_temp_diff(fig, plot_diff, agent_data, roomid)
    plot_room_power_available(fig, plot_pa, agent_data, roomid)
    plot_room_power_consumed(fig, plot_pc, agent_data, roomid)

    print(agent_data)


def plot_room_temp(fig: plt.Figure, plot: plt.Axes, agent_data: pl.DataFrame, roomid: str):
    """This function assumes that agent_data contains records only for agent with id roomid"""
    x_data = agent_data.get_column(COL_TIME)
    y_temp = agent_data.get_column(COL_TEMP)
    y_temp_default = agent_data.get_column(COL_DEF_TEMP)
    y_temp_target = agent_data.get_column(COL_TARGET_TEMP)

    plot.plot(x_data, y_temp, linestyle=PRIMARY_LINE_STYLE,
              marker=PRIMARY_MARK_STYLE, label='Temperatura')
    plot.plot(x_data, y_temp_default, linestyle=SECONDARY_LINE_STYLE,
              label='Temperatura tła')
    plot.plot(x_data, y_temp_target, linestyle=SECONDARY_LINE_STYLE,
              label='Temperatura docelowa')

    plot.set(
        title='Temperatura w pomieszczeniu w zależności od czasu',
        xlabel='Czas [tick]',
        ylabel='Temperatura [st. C]',
    )
    plot.legend()
    plot.grid()


def plot_room_temp_diff(fig: plt.Figure, plot: plt.Axes, agent_data: pl.DataFrame, roomid: str):
    """This function assumes that agent_data contains records only for agent with id roomid"""
    x_data = agent_data.get_column(COL_TIME)
    y_data = (agent_data[COL_TEMP] - agent_data[COL_TARGET_TEMP]).abs()

    plot.plot(x_data, y_data, linestyle=PRIMARY_LINE_STYLE,
              marker=PRIMARY_MARK_STYLE, label='Różnica')

    plot.set(
        title='Różnica pomiędzy temperaturą początkową a docelową w czasie (wartość bezwzlęgna)',
        xlabel='Czas [tick]',
        ylabel='Różnica [st. C]',
    )
    plot.legend()
    plot.grid()


def plot_room_power_available(fig: plt.Figure, plot: plt.Axes, agent_data: pl.DataFrame, roomid: str):
    """This function assumes that agent_data contains records only for agent with id roomid"""
    x_data = agent_data.get_column(COL_TIME)
    y_data = agent_data.get_column(COL_POW_AV)

    plot.plot(x_data, y_data, linestyle=PRIMARY_LINE_STYLE,
              marker=PRIMARY_MARK_STYLE, label='Dostępna moc')

    plot.set(
        title='Moc dostępna dla danego agenta w czasie',
        xlabel='Czas [tick]',
        ylabel='Dostępna moc [W]',
    )
    plot.legend()
    plot.grid()


def plot_room_power_consumed(fig: plt.Figure, plot: plt.Axes, agent_data: pl.DataFrame, roomid: str):
    """This function assumes that agent_data contains records only for agent with id roomid"""
    x_data = agent_data.get_column(COL_TIME)
    y_data = agent_data.get_column(COL_POW_CONS)

    plot.plot(x_data, y_data, linestyle=PRIMARY_LINE_STYLE,
              marker=PRIMARY_MARK_STYLE, label='Wykorzystana moc')
    plot.set(
        title='Moc wykorzystana przez agenta w danym momencie czasowym',
        xlabel='Czas [tick]',
        ylabel='Wykorzystana moc [W]'
    )
    plot.legend()
    plot.grid()

