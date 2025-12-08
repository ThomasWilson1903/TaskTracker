using Avalonia.Controls;
using Microsoft.Extensions.DependencyInjection;
using TaskTracker.Services;
using TaskTracker.ViewModels;

namespace TaskTracker.Views;

public partial class MainWindow : Window
{
    public MainWindow(MainViewModel viewModel)
    {
        InitializeComponent();

        MainPanel.Children.Add(new MainView(App.Services.GetRequiredService<INavigationService>(), viewModel));
    }
}