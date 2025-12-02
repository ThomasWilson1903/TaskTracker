using System;
using Avalonia.Controls;
using Avalonia.Controls.Templates;
using Microsoft.Extensions.DependencyInjection;
using TaskTracker.ViewModels;

namespace TaskTracker;

public class ViewLocator : IDataTemplate
{
    private static IServiceProvider? _serviceProvider;

    public static void Initialize(IServiceProvider serviceProvider)
    {
        _serviceProvider = serviceProvider;
    }

    public Control? Build(object? param) =>
        Build(param, Array.Empty<object>());
    

    public static Control? Build(object? param, params object[] parameters)
    {
        if (_serviceProvider is null)
            throw new InvalidOperationException(Localization.Errors.ViewLocatorNotInitialized);

        if (param is null)
            return null;

        var name = param.GetType().FullName!.Replace("ViewModel", "View", StringComparison.Ordinal);
        var type = Type.GetType(name);

        if (type is not null)
        {
            return parameters is not null && parameters.Length > 0
                ? (Control)ActivatorUtilities.CreateInstance(_serviceProvider, type, parameters)
                : (Control)ActivatorUtilities.CreateInstance(_serviceProvider, type);
        }

        return new TextBlock { Text = $"{Localization.Resources.NotFound}: {name}" };
    }

    public bool Match(object? data)
    {
        return data is ViewModelBase;
    }
}