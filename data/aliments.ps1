$csv = Import-Csv .\aliments.csv -Delimiter ',' -Encoding "utf8" -Header A

$query = "insert into aliments (name) values "
$stringBuilder = New-Object System.Text.StringBuilder
$null = $stringBuilder.Append($query)


$csv | foreach {
    $A = $_.A.replace("'", "\'")
    $null = $stringBuilder.Append("(`'$A`'),`r`n")
}

$query = $stringBuilder.ToString()
$query = $query.Substring(0, $query.Length - 3)

$query > .\aliments.sql