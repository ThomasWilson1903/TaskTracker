using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using TaskTracker.ViewModels;

namespace TaskTracker.Views;

public partial class TaskListView : UserControl
{
    private TaskListViewModel _viewModel;

    public TaskListView(TaskListViewModel viewModel)
    {
        _viewModel = viewModel;
        DataContext = _viewModel;

        InitializeComponent();
    }

    private void DeleteButton_Click(object? sender, Avalonia.Interactivity.RoutedEventArgs e)
    {
        var button = sender as Button;
        if (button is null)
            return;

        var item = button.DataContext as TaskListViewModel;
        if (item is null)
            return;

        _viewModel.DeleteTaskCommand.Execute(item);
    }
}