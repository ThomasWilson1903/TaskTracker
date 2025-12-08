using System;
using Avalonia.Controls;
using Avalonia.Controls.Templates;
using Microsoft.Extensions.DependencyInjection;
using TaskTracker.ViewModels;

namespace TaskTracker;

public class ViewLocator : IDataTemplate
{
    public Control? Build(object? param) =>
        Build(param, Array.Empty<object>());
    

    public static Control? Build(object? param, params object[] parameters)
    {
        var p = param as Type;
        if (p is null)
            return null;

        var name = p.FullName!.Replace("ViewModel", "View", StringComparison.Ordinal);
        var type = Type.GetType(name);

        if (type is not null)
        {
            return parameters is not null && parameters.Length > 0
                ? (Control)ActivatorUtilities.CreateInstance(App.Services, type, parameters)
                : (Control)ActivatorUtilities.CreateInstance(App.Services, type);
        }

        return new TextBlock { Text = $"{Localization.Resources.NotFound}: {name}" };
    }

    public bool Match(object? data)
    {
        return data is ViewModelBase;
    }
}