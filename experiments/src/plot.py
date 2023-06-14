import polars as pl
import matplotlib.pyplot as plt


def plot_metric(fig: plt.Figure, plot: plt.Axes, raw_data: pl.DataFrame):
    """ General data should be massed as raw_data """

    x_data = raw_data.get_column('time')
    y_data = raw_data.get_column('metric')
    plot.plot(x_data, y_data, linestyle='--', marker='o', label='Metryka')
    plot.plot(x_data, [0 for _ in range(len(y_data))],
              linestyle=':', label='Wartość idealna, $y = 0$')

    plot.set(
        title="Wartość metryki od ticku czasowego",
        xlabel="Tick czasowy",
        ylabel="Wartość metryki"
    )
    plot.legend()
    plot.grid()


def plot_room(fig: plt.Figure, plot_temp: plt.Axes, plot_diff: plt.Axes,
              plot_pa: plt.Axes, plot_pc: plt.Axes, agent_data: pl.DataFrame, roomid: str):
    agent_data = agent_data.filter(pl.col('roomid') == roomid).sort('time')

    fig.suptitle(f'Analiza dla agenta "{roomid}"')
    plot_room_temp(fig, plot_temp, agent_data, roomid)
    plot_room_temp_diff(fig, plot_diff, agent_data, roomid)
    plot_room_power_available(fig, plot_pa, agent_data, roomid)
    plot_room_power_consumed(fig, plot_pc, agent_data, roomid)

    print(agent_data)


def plot_room_temp(fig: plt.Figure, plot: plt.Axes, agent_data: pl.DataFrame, roomid: str):
    """This function assumes that agent_data contains records only for agent with id roomid"""

    plot.set(
        title='Temperatura w pomieszczeniu w zależności od czasu',
        xlabel='Czas [tick]',
        ylabel='Temperatura [st. C]',
    )


def plot_room_temp_diff(fig: plt.Figure, plot: plt.Axes, agent_data: pl.DataFrame, roomid: str):
    """This function assumes that agent_data contains records only for agent with id roomid"""
    plot.set(
        title='Różnica pomiędzy temperaturą początkową a docelową w czasie',
        xlabel='Czas [tick]',
        ylabel='Różnica [st. C]',
    )


def plot_room_power_available(fig: plt.Figure, plot: plt.Axes, agent_data: pl.DataFrame, roomid: str):
    """This function assumes that agent_data contains records only for agent with id roomid"""
    plot.set(
        title='Moc dostępna dla danego agenta w czasie',
        xlabel='Czas [tick]',
        ylabel='Dostępna moc [W]',
    )


def plot_room_power_consumed(fig: plt.Figure, plot: plt.Axes, agent_data: pl.DataFrame, roomid: str):
    """This function assumes that agent_data contains records only for agent with id roomid"""
    plot.set(
        title='Moc wykorzystana przez agenta w danym momencie czasowym',
        xlabel='Czas [tick]',
        ylabel='Wykorzystana moc [W]'
    )

