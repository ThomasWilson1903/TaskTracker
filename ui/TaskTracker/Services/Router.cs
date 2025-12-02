using System;
using System.Collections.Generic;
using System.Globalization;

namespace TaskTracker.Services;

internal class Router : IRouter, IRouteBuilder
{
    private readonly INavigationService _navigationService;
    private readonly Dictionary<string, Route> _routes = new(StringComparer.OrdinalIgnoreCase);

    private Route? _currentRoute;

    public Route? CurrentRoute => _currentRoute;

    public Router(INavigationService navigationService)
    {
        _navigationService = navigationService;

        _navigationService.Navigated += _navigationService_Navigated;
    }

    public void AddRoute(string route, Type type)
    {
        if (!_routes.ContainsKey(route))
            _routes.Add(route, new Route(route, type));
    }

    public void ConfigureRoutes(Action<IRouteBuilder> configure)
    {
        if (configure is null)
            return;

        configure(this);
    }

    public object? NavigateTo(string path, params object[] parameters)
    {
        if (!_routes.TryGetValue(path, out var route))
            throw new InvalidOperationException(string.Format(CultureInfo.CurrentCulture, Localization.Errors.PathIsNotConfigured, path));

        var content = ViewLocator.Build(route.Type, parameters);
        if (content is null)
            return null;

        _currentRoute = route;
        _navigationService.NavigateTo(content);
        return content;
    }

    public Type? ResolveType(string path) =>
        _routes.TryGetValue(path, out var route) ? route.Type : null;

    public IEnumerable<KeyValuePair<string, Route>> GetRoutes() =>
        _routes;

    public Route? GetRoute(string path) =>
        _routes.TryGetValue(path, out var route) ? route : null;

    private void _navigationService_Navigated(object? sender, NavigationEventArgs e)
    {
        var contentType = e.Content.GetType();
        if (contentType is null)
            return;

        foreach (var kv in _routes)
        {
            if (kv.Value.Type.Name == contentType.Name + "Model")
            {
                _currentRoute = kv.Value;
                break;
            }
        }
    }
}
