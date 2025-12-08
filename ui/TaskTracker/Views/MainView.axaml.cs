using Avalonia.Controls;
using TaskTracker.Services;
using TaskTracker.ViewModels;

namespace TaskTracker.Views;

public partial class MainView : UserControl
{
    public MainView(INavigationService navigationService, MainViewModel viewModel)
    {
        InitializeComponent();

        navigationService.SetRoot(NavControl);

        viewModel.GoToAuthorizationCommand.Execute(null);
    }
}