using Avalonia.Controls;
using System;
using TaskTracker.ViewModels;

namespace TaskTracker;

public partial class CreateTaskWindow : Window
{
    public TaskViewModel? Result { get; set; }

    public CreateTaskWindow()
    {
        InitializeComponent();
    }

    private void SaveButton_Click(object? sender, Avalonia.Interactivity.RoutedEventArgs e)
    {
        var dueDate = DueDatePicker.SelectedDate;
        var dueTime = DueTimePicker.SelectedTime;
        DateTime? dueDateTime = dueDate.HasValue && dueTime.HasValue
            ? new DateTime(DateOnly.FromDateTime(dueDate.Value.DateTime), TimeOnly.FromTimeSpan(dueTime.Value))
            : null;
        if (!string.IsNullOrEmpty(Title.Text) &&
            !string.IsNullOrEmpty(Description.Text) &&
            dueDateTime is DateTime ddt)
        {
            Result = new TaskViewModel()
            {
                Title = Title.Text,
                Description = Description.Text,
                Priority = "High",
                DueDate = ddt,
            };
            Close();
        }
    }

    private void CancelButton_Click(object? sender, Avalonia.Interactivity.RoutedEventArgs e)
    {
        Result = null;
        Close();
    }
}